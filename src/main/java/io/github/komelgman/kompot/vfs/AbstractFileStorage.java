package io.github.komelgman.kompot.vfs;

public abstract class AbstractFileStorage<RootType> implements FileStorage<RootType> {
    private RootType root;

    protected AbstractFileStorage(RootType root) {
        this.root = root;
    }

    @Override
    public RootType getRoot() {
        return root;
    }

    public boolean equals(Object o) {
        return getRoot().equals(o);
    }

    public int hashCode() {
        return getRoot().hashCode();
    }
}
