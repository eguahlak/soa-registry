package dk.cphbusiness.soa;

import dk.cphbusiness.soa.contract.NoMatchingServiceException;
import dk.cphbusiness.soa.contract.RegistryContract;
import dk.cphbusiness.soa.contract.ServiceContract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleRegistry implements RegistryContract {
  private Comparator<ServiceContract> priorityComparator =
      new ServicePriorityComparator();
  private Map<Class, List<ServiceContract>> contracts = 
      new HashMap<Class, List<ServiceContract>>();

  @Override
  public <C extends ServiceContract> void register(
      Class<C> contract, C service
      ) {
    List<ServiceContract> services = contracts.get(contract);
    if (services == null) {
      services = new ArrayList<ServiceContract>();
      contracts.put(contract, services);
      }
    services.add(service);
    }

  @Override
  public <C extends ServiceContract> C lookup(
      Class<C> contract, String... arguments
      ) throws NoMatchingServiceException {
    List<C> services = (List<C>)contracts.get(contract);
    if (services == null)
        throw new NoMatchingServiceException("No service registered");
    Collections.sort(services, priorityComparator);
    searchServices: for (C service : services) {
      for (String argument : arguments) {
        if (!service.supports(argument)) continue searchServices;
        }
      return service;
      }
    throw new NoMatchingServiceException("No services mathes the criteria");
    }

  private class ServicePriorityComparator
      implements Comparator<ServiceContract> {

    @Override
    public int compare(ServiceContract c1, ServiceContract c2) {
      if (c1.getPriority() < c2.getPriority()) return -1;
      if (c1.getPriority() == c2.getPriority()) return 0;
      return 1;
      }
    
    }
  
  }
