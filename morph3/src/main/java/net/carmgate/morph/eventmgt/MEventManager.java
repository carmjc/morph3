package net.carmgate.morph.eventmgt;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

@Singleton
public class MEventManager {
   private final Map<Type, Set<Method>> observingMethodsMapByOwnerClass = new HashMap<>();
   private final Map<Type, Set<Method>> observingMethodsMapByEvent = new HashMap<>();
   private final Map<Type, Set<Object>> instances = new HashMap<>();
   private final List<Object> tmp = new ArrayList<>();
   private boolean firingEvent;
   private boolean scanning;
   private Map<Type, List<Object>> deferredEvents = new HashMap<>();

   public void scanAndRegister(Object o) {
      if (firingEvent) {
         tmp.add(o);
         return;
      }

      // Look for @MObserves
      Set<Method> observingMethods = observingMethodsMapByOwnerClass.get(o.getClass());
      if (observingMethods == null) {
         observingMethods = new HashSet<>();
         Method[] declaredMethods = o.getClass().getDeclaredMethods();
         for (Method method : declaredMethods) {
            if (method.getAnnotatedParameterTypes().length > 0) {
               observingMethods.add(method);
            }
         }

         observingMethodsMapByOwnerClass.put(o.getClass(), observingMethods);

         observingMethods.forEach(m -> {
            for (AnnotatedType type : m.getAnnotatedParameterTypes()) {
               if (type.isAnnotationPresent(MObserves.class)) {
                  Set<Method> methods = observingMethodsMapByEvent.get(type.getType());
                  if (methods == null) {
                     methods = new HashSet<>();
                     observingMethodsMapByEvent.put(type.getType(), methods);
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

   public void addEvent(Object o) {
      List<Object> list = deferredEvents.get(o.getClass());
      if (list == null) {
         list = new ArrayList<>();
         deferredEvents.put(o.getClass(), list);
      }
      list.add(o);
   }

   public void deferredFire() {
      for (Type type : deferredEvents.keySet()) {

         setFiringEvent(true);
         getObservingMethodsMapByEvent().get(event.getClass()).forEach(method -> {
            eventManager.getInstances().get(method.getClass()).forEach(object -> {
               try {
                  method.invoke(object, event);
               } catch (Exception e) {
                  throw new EventManagementException(e);
               }
            });
         });
         setFiringEvent(false);
      }

      getTmp().forEach(o -> {
         scanAndRegister(o);
      });
   }

   public boolean isFiringEvent() {
      return firingEvent;
   }

   public void setFiringEvent(boolean firingEvent) {
      this.firingEvent = firingEvent;
   }

   public Map<Type, Set<Method>> getObservingMethodsMapByOwnerClass() {
      return observingMethodsMapByOwnerClass;
   }

   public Map<Type, Set<Method>> getObservingMethodsMapByEvent() {
      return observingMethodsMapByEvent;
   }

   public Map<Type, Set<Object>> getInstances() {
      return instances;
   }

   public List<Object> getTmp() {
      return tmp;
   }

   public boolean isScanning() {
      return scanning;
   }

   public void setScanning(boolean scanning) {
      this.scanning = scanning;
   }

}
