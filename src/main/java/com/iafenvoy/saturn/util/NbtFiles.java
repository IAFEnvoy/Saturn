package com.iafenvoy.saturn.util;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * File framing is independent of FileAccess initialization; readers accept both compression modes and root headers.
 */
final class NbtFiles {
    private NbtFiles() {
    }

    static Tag read(InputStream stream) throws IOException {
        byte[] bytes = stream.readAllBytes();
        // GZIP magic cannot be a valid NBT root type, so compression can be detected without a failing parse.
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0x1F && (bytes[1] & 0xFF) == 0x8B) {
            try (InputStream input = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
                bytes = input.readAllBytes();
            }
        }
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes))) {
            input.mark(bytes.length);
            try {
                // Probe the old header first; standard compound roots stop immediately at their empty root name.
                return readTag(input, true);
            } catch (Exception legacyError) {
                input.reset();
                try {
                    return readTag(input, false);
                } catch (Exception standardError) {
                    IOException error = new IOException("Unable to read standard or legacy NBT", standardError);
                    error.addSuppressed(legacyError);
                    throw error;
                }
            }
        }
    }

    private static Tag readTag(DataInputStream input, boolean legacy) throws IOException {
        Tag tag = legacy ? NbtIo.readAnyTag(input, NbtAccounter.unlimitedHeap())
                : NbtIo.readUnnamedTag(input, NbtAccounter.unlimitedHeap());
        // A wrong header can appear to parse successfully while leaving most of the document unread.
        if (input.read() != -1) throw new IOException("Trailing data after NBT root");
        return tag;
    }

    static void write(Tag tag, OutputStream stream, boolean compressed) throws IOException {
        try (DataOutputStream output = new DataOutputStream(compressed ? new GZIPOutputStream(stream) : stream)) {
            // Standard file NBT always includes the root name, empty here, regardless of compression.
            NbtIo.writeUnnamedTag(tag, output);
        }
    }
}
