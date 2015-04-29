load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model)
importPackage(Packages.net.carmgate.morph.model.entities.physical.ship)
importPackage(Packages.net.carmgate.morph.model.entities.physical)
importPackage(Packages.net.carmgate.morph.model.geometry)
importPackage(Packages.net.carmgate.morph.model.orders)
importPackage(Packages.org.lwjgl.util.vector)
var ArrayList = Java.type('java.util.ArrayList');

me = new Player("Me");
me.color = [1, 0.5, 0.5, 1];
other = new Player("Other");
other.color = [0.2, 1, 0.5, 1];

asteroid = entityFactory.newInstance(PhysicalEntityType.valueOf("ASTEROID"));
asteroid.getPos().copy(-500, -80);
asteroid.mass = 2;
asteroid.rotateSpeed = 5;
world.add(asteroid);

ship1 = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
ship1.getPos().copy(100, 0);
ship1.health = 10;
ship1.mass = 2;
ship1.player = me;
ship1.energy = 100;
ship1.getComponents().put(ComponentType.GENERATORS, new SimpleGenerator(ship1));
world.add(ship1);

ship = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
ship.getPos().copy(-200, 200);
ship.health = 10;
ship.mass = 0.5;
ship.player = other;
ship.energy = 10;
ship.resources = 10;
ship.getSpeed().copy(-100, -100)
ship.getComponents().put(ComponentType.MINING_LASERS, new MiningLaser(ship));
ship.getComponents().put(ComponentType.LASERS, new Laser(ship));
ship.getComponents().put(ComponentType.PROPULSORS, new SimplePropulsor(ship));
ship.getComponents().put(ComponentType.GENERATORS, new SimpleGenerator(ship));
//order = orderFactory.newInstance(OrderType.MINE_ASTEROID, ship);
//order.setAsteroid(asteroid);
order = orderFactory.newInstance(OrderType.ATTACK, ship);
order.setTarget(ship1);
ship.add(order);
world.add(ship);

//ship1 = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
//ship1.getPos().copy(0, 0);
//ship1.setHealth(10);
//ship1.mass = 0.2;
//ship1.player = me;
//ship1.getSpeed().copy(100, -100)
//closein = orderFactory.newInstance(OrderType.CLOSE_IN);
//closein.setOrderee(ship1);
//closein.setTarget(ship);
//closein.setDistance(100);
//ship1.add(closein);
//world.add(ship1);

//ship = entityFactory.newInstance(PhysicalEntityType.valueOf("SHIP"));
//ship.getPos().copy(100, 0);
//ship.setHealth(10);
//ship.mass = 0.5;
//ship.player = other;
//world.add(ship);
