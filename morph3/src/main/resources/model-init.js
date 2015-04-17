load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model)
importPackage(Packages.net.carmgate.morph.model.entities)
importPackage(Packages.org.lwjgl.util.vector)
var ArrayList = Java.type('java.util.ArrayList');

me = new Player("Me");
me.color = [1, 0.5, 0.5, 1];
other = new Player("Other");
other.color = [0.2, 1, 0.5, 1];

ship1 = new Ship(new Vector2f(0, 0));
ship1.mass = 0.2;
ship1.owner = me;
model.add(ship1);

ship2 = new Ship(new Vector2f(100, 0));
ship2.mass = 0.5;
ship2.owner = other;
var fleds = new ArrayList();
fleds.add(ship1);
ship2.add(new Flee(ship2, fleds));
model.add(ship2);
