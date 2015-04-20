package net.carmgate.morph.eventmgt;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.DeadShip;

@Singleton
public class MEventManager {
   private final Map<Type, Set<Method>> observingMethodsMapByOwnerClass = new HashMap<>();
   private final Map<Type, Set<Method>> observingMethodsMapByEvent = new HashMap<>();
   private final Map<Type, Set<Object>> instances = new HashMap<>();
   private final List<Object> tmp = new ArrayList<>();
   private boolean firingEvent;
   private boolean scanning;
   private Map<Type, List<Object>> deferredEvents = new HashMap<>();
   private Map<Type, List<Object>> deferredEventsBeingHandled = new HashMap<>();

   public void addEvent(Object o) {
      List<Object> list = deferredEvents.get(o.getClass());
      if (list == null) {
         list = new ArrayList<>();
         deferredEvents.put(o.getClass(), list);
      }
      list.add(o);
   }

   @PostConstruct
   protected void registerWithMEventManager() {
      scanAndRegister(this);
   }

   protected void onDeadShip(@MObserves DeadShip deadShip) {
      instances.get(Ship.class).remove(deadShip.getShip());
   }

   public void deferredFire() {
      setFiringEvent(true);

      synchronized (deferredEvents) {
         Map<Type, List<Object>> tmp = deferredEventsBeingHandled;
         deferredEventsBeingHandled = deferredEvents;
         deferredEvents = tmp;
      }

      final Set<Type> keySet = deferredEventsBeingHandled.keySet();
      for (final Type type : keySet) {
         // for each observing method
         Set<Method> observingMethods = observingMethodsMapByEvent.get(type);
         if (observingMethods == null) {
            continue;
         }
         observingMethods.forEach(method -> {
            // for each observing bean
            instances.get(method.getDeclaringClass()).forEach(object -> {
               // for each event
               deferredEventsBeingHandled.get(type).forEach(event -> {
                  try {
                     boolean isAccessible = method.isAccessible();
                     method.setAccessible(true);
                     method.invoke(object, event);
                     method.setAccessible(isAccessible);
                  } catch (final Exception e) {
                     throw new EventManagementException(e);
                  }
               });
            });
         });
      }
      setFiringEvent(false);

      deferredEventsBeingHandled.clear();
      // TODO call the deferredFiring again if there were new events while we are handling old one
      // But we should not do the scanAndRegister twice ..

      // Once we're done firing, scan and register events that were not registered because of the firing
      getTmp().forEach(o -> {
         scanAndRegister(o);
      });
      getTmp().clear();
   }

   public Map<Type, List<Object>> getDeferredEvents() {
      return deferredEvents;
   }

   public Map<Type, Set<Object>> getInstances() {
      return instances;
   }

   public Map<Type, Set<Method>> getObservingMethodsMapByEvent() {
      return observingMethodsMapByEvent;
   }

   public Map<Type, Set<Method>> getObservingMethodsMapByOwnerClass() {
      return observingMethodsMapByOwnerClass;
   }

   public List<Object> getTmp() {
      return tmp;
   }

   public boolean isFiringEvent() {
      return firingEvent;
   }

   public boolean isScanning() {
      return scanning;
   }

   public void scanAndRegister(Object o) {
      if (firingEvent) {
         tmp.add(o);
         return;
      }

      // Look for @MObserves
      Set<Method> observingMethods = observingMethodsMapByOwnerClass.get(o.getClass());
      if (observingMethods == null) {
         observingMethods = new HashSet<>();
         final Method[] declaredMethods = o.getClass().getDeclaredMethods();
         for (final Method method : declaredMethods) {
            for (Parameter param : method.getParameters()) {
               if (param.isAnnotationPresent(MObserves.class)) {
                  observingMethods.add(method);
               }
            }
         }

         observingMethodsMapByOwnerClass.put(o.getClass(), observingMethods);

         observingMethods.forEach(m -> {
            for (final Parameter param : m.getParameters()) {
               if (param.isAnnotationPresent(MObserves.class)) {
                  Set<Method> methods = observingMethodsMapByEvent.get(param.getType());
                  if (methods == null) {
                     methods = new HashSet<>();
                     observingMethodsMapByEvent.put(param.getType(), methods);
                  }
                  methods.add(m);
               }
            }
         });
      }

      // add the object to the observers
      Set<Object> oTypeInstances = instances.get(o.getClass());
      if (oTypeInstances == null) {
         oTypeInstances = new HashSet<>();
         instances.put(o.getClass(), oTypeInstances);
      }
      oTypeInstances.add(o);
   }

   public void setFiringEvent(boolean firingEvent) {
      this.firingEvent = firingEvent;
   }

   public void setScanning(boolean scanning) {
      this.scanning = scanning;
   }

}
