load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model)
importPackage(Packages.net.carmgate.morph.model.entities.physical.ship.components)
importPackage(Packages.net.carmgate.morph.model.entities.physical.ship)
importPackage(Packages.net.carmgate.morph.model.entities.physical)
importPackage(Packages.net.carmgate.morph.model.geometry)
//importPackage(Packages.net.carmgate.morph.orders)
importPackage(Packages.org.lwjgl.util.vector)
var ArrayList = Java.type('java.util.ArrayList');

me = new Player("Me");
me.color = [1, 0.5, 0.5, 1];
world.add(me);
other = new Player("Other");
other.color = [0.2, 1, 0.5, 1];
world.add(other);

asteroid = entityFactory.newInstance(PhysicalEntityType.valueOf("ASTEROID"));
asteroid.getPos().copy(-500, -80);
asteroid.mass = 2000;
asteroid.rotateSpeed = 5;
world.add(asteroid);

ship = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
ship.getPos().copy(-200, 200);
ship.mass = 0.5;
ship.durability = 100;
ship.player = me;
ship.energy = 1;
ship.resources = 10;
//ship.getSpeed().copy(-100, -100)
ship.add(componentFactory.newInstance(MiningLaser.class), 1.0 / 16);
ship.add(componentFactory.newInstance(SimplePropulsor.class), 1.0 / 4);
ship.add(componentFactory.newInstance(SimpleGenerator.class), 1.0 / 8);
ship.add(componentFactory.newInstance(SimpleRepairer.class), 1.0 / 16);
ship.add(componentFactory.newInstance(Laser.class), 1.0 / 2);
//order = orderFactory.newInstance(OrderType.MINE_ASTEROID, ship);
//order.setAsteroid(asteroid);
//ship.add(order);
world.add(ship);

