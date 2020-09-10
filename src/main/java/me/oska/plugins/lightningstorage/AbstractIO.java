package me.oska.plugins.lightningstorage;

import de.leonhard.storage.Json;

import java.io.File;

public class AbstractIO {

    private Json json;

    public AbstractIO(File file) {
        this.json = new Json(file);
    }

    public AbstractIO prefix(String prefix) {
        AbstractIO io = new AbstractIO(this.json.getFile());
        io.json.setPathPrefix(prefix);
        return io;
    }

    public void create(String path, Object value) {
        this.json.set(path, value);
    }

    public <T> T find(String path, T def) {
        return this.json.get(path, def);
    }

    public void edit(String path, Object value) {
        this.json.set(path, value);
    }

    public void remove(String path) {
        this.json.remove(path);
    }

    public void reload() {
        this.json.forceReload();
    }
}
