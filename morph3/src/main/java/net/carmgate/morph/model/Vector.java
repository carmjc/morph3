package net.carmgate.morph.model;

public interface Vector<V> {

   V add(V v);

   V sub(V v);

   V scale(float factor);

   V copy(V v);
}
