package com.iafenvoy.saturn.runtime.resulting;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * What an action answered when it refused: why, and which resource the reason was about. Going through is the same
 * type with both empty, so a caller reads any result the same way - {@link #getFailure()} being empty is the answer.
 *
 * <p>A class rather than a record on purpose: a result that also carries state of its own extends this and adds its
 * own fields, so the two questions above are answered in one place for every kind of result.
 */
public sealed interface Result<T> permits Result.SuccessResult, Result.FailureResult {
    static <T> Result<T> success(@NotNull T value) {
        return new SuccessResult<>(value);
    }

    static Result<Void> success() {
        return new SuccessResult<>(null);
    }

    static <T> Result<T> failure(@NotNull Failure failure) {
        return new FailureResult<>(failure);
    }

    static <T> Result<T> failure(@NotNull Failure failure, @Nullable Identifier resource) {
        return new FailureResult<>(resource == null ? failure : failure.withResource(resource));
    }

    Either<T, Failure> either();

    Optional<T> getData();

    Optional<Failure> getFailure();

    <N> Result<N> mapData(Function<? super T, ? extends N> l);

    default <C, D> Either<C, D> mapBoth(Function<? super T, ? extends C> f1, Function<? super Failure, ? extends D> f2) {
        return this.either().mapBoth(f1, f2);
    }

    default <T1> T1 map(Function<? super T, ? extends T1> l, Function<? super Failure, ? extends T1> r) {
        return this.either().map(l, r);
    }

    default Result<T> ifSuccess(Consumer<? super T> consumer) {
        this.getData().ifPresent(consumer);
        return this;
    }

    default Result<T> ifFailure(Consumer<? super Failure> consumer) {
        this.getFailure().ifPresent(consumer);
        return this;
    }

    default <F> Either<T, F> mapFailure(Function<Failure, ? extends F> l) {
        return this.either().mapRight(l);
    }

    default T orThrow() {
        return this.map(l -> l, r -> {
            if (r instanceof Throwable) throw new RuntimeException((Throwable) r);
            throw new RuntimeException(r.toString());
        });
    }

    record SuccessResult<T>(T data) implements Result<T> {
        @Override
        public @NotNull T data() {
            if (this.data == null) throw new RuntimeException("Cannot get data when result is Void");
            return this.data;
        }

        @Override
        public Either<T, Failure> either() {
            return Either.left(this.data);
        }

        @Override
        public Optional<T> getData() {
            return Optional.ofNullable(this.data);
        }

        @Override
        public Optional<Failure> getFailure() {
            return Optional.empty();
        }

        @Override
        public <N> Result<N> mapData(Function<? super T, ? extends N> l) {
            return success(l.apply(this.data));
        }
    }

    record FailureResult<T>(@NotNull Failure failure) implements Result<T> {
        @Override
        public Either<T, Failure> either() {
            return Either.right(this.failure);
        }

        @Override
        public Optional<T> getData() {
            return Optional.empty();
        }

        @Override
        public Optional<Failure> getFailure() {
            return Optional.of(this.failure);
        }

        @Override
        public <N> Result<N> mapData(Function<? super T, ? extends N> l) {
            return Result.failure(this.failure);
        }
    }
}
