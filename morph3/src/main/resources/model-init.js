load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model)
importPackage(Packages.net.carmgate.morph.model.entities)
importPackage(Packages.org.lwjgl.util.vector)

me = new Player("Me");
me.color = [1, 0.5, 0.5, 1];
other = new Player("Other");
other.color = [0.2, 1, 0.5, 1];

ship = new Ship(new Vector2f(0, 0));
ship.mass = 0.2;
ship.owner = me;
model.add(ship);

ship = new Ship(new Vector2f(100, 0));
ship.mass = 0.5;
ship.owner = other;
model.add(ship);
