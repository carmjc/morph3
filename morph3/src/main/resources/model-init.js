load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model)
importPackage(Packages.net.carmgate.morph.model.entities.physical)
importPackage(Packages.net.carmgate.morph.model.geometry)
importPackage(Packages.net.carmgate.morph.model.orders)
importPackage(Packages.org.lwjgl.util.vector)
var ArrayList = Java.type('java.util.ArrayList');

me = new Player("Me");
me.color = [1, 0.5, 0.5, 1];
other = new Player("Other");
other.color = [0.2, 1, 0.5, 1];

ship1 = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
ship1.init(new Vector2f(0, 0), 10);
ship1.mass = 0.2;
ship1.player = me;
world.add(ship1);

ship = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
ship.init(new Vector2f(100, 0), 10);
ship.mass = 0.5;
ship.player = other;
world.add(ship);

ship = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
ship.init(new Vector2f(150, 250), 10);
ship.mass = 0.5;
ship.player = other;
world.add(ship);
