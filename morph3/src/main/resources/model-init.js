load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model.entities)
importPackage(Packages.org.lwjgl.util.vector)

newShip = new Ship(new Vector2f(0, 0));
model.getShips().add(newShip);
newShip = new Ship(new Vector2f(100, 0));
model.getShips().add(newShip);
