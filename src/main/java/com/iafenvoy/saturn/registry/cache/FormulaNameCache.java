package com.iafenvoy.saturn.registry.cache;

import com.iafenvoy.saturn.util.HolderHelper;
import com.iafenvoy.saturn.util.formula.FormulaVariables;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class FormulaNameCache<T> extends RegistryCache<Map<String, Holder<T>>> {
    private final ResourceKey<Registry<T>> registry;
    private final String kind;
    private final Map<String, Holder<T>> data = new LinkedHashMap<>();

    public FormulaNameCache(ResourceKey<Registry<T>> registry, String kind) {
        this.registry = registry;
        this.kind = kind;
    }

    @Override
    protected void build(RegistryAccess access) {
        Map<String, List<Identifier>> aliases = new LinkedHashMap<>();
        access.lookupOrThrow(this.registry).listElements().forEach(holder -> {
            Identifier id = HolderHelper.id(holder);
            this.data.put(this.exactName(id), holder);
            String alias = flatten(id);
            aliases.computeIfAbsent(alias, ignored -> new ArrayList<>()).add(id);
            this.data.putIfAbsent(alias, holder);
        });
        aliases.forEach((alias, ids) -> {
            if (ids.size() == 1) return;
            this.data.remove(alias);
            FormulaVariables.LOGGER.log("Ambiguous formula " + this.kind + " name '" + alias + "' for " + ids
                    + "; use exact names " + ids.stream().map(id -> "caster_" + this.exactName(id)).toList());
        });
    }

    @Override
    protected void clear() {
        this.data.clear();
    }

    @Override
    public Map<String, Holder<T>> get() {
        return this.data;
    }

    @Nullable
    public Holder<T> get(String key) {
        return this.data.get(key);
    }

    /**
     * A unique formula suffix for this registry kind and identifier. The uppercase prefix is outside the legacy
     * alias alphabet.
     */
    public String exactName(Identifier id) {
        return "ID_" + this.kind + "_" + HexFormat.of().formatHex(id.toString().getBytes(StandardCharsets.UTF_8));
    }

    // The flat id shape every formula name uses: exp4j identifiers cannot hold / . -
    public static String flatten(Identifier id) {
        return (id.getNamespace() + "_" + id.getPath()).replace('/', '_').replace('.', '_').replace('-', '_');
    }
}
