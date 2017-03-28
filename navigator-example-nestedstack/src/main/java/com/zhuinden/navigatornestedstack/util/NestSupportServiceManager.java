package com.zhuinden.navigatornestedstack.util;

import android.content.Context;
import android.util.Log;

import com.zhuinden.navigator.Navigator;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.statebundle.StateBundle;

import java.util.LinkedList;
import java.util.List;

public class NestSupportServiceManager {
    public static final String SERVICE_MANAGER = "SERVICE_MANAGER";

    public static NestSupportServiceManager get(Context context) {
        //noinspection ResourceType
        return (NestSupportServiceManager) context.getSystemService(SERVICE_MANAGER);
    }

    private final ServiceTree serviceTree;

    public NestSupportServiceManager(ServiceTree serviceTree) {
        this.serviceTree = serviceTree;
    }

    public static final String SERVICE_STATES = "SERVICE_BUNDLE";

    private static final String TAG = "NestServiceManager";

    public StateBundle persistStates() {
        StateBundle serviceStates = new StateBundle();
        serviceTree.traverseTree(ServiceTree.Walk.PRE_ORDER, node -> {
            StateBundle keyBundle = new StateBundle();
            for(ServiceTree.Node.Entry entry : node.getBoundServices()) {
                if(entry.getService() instanceof Bundleable) {
                    keyBundle.putParcelable(entry.getName(), ((Bundleable) entry.getService()).toBundle());
                }
            }
            serviceStates.putParcelable(node.getKey().toString(), keyBundle);
        });
        return serviceStates;
    }

    public void setupServices(StateChange stateChange) {
        StateBundle states = serviceTree.getRootService(SERVICE_STATES);
        for(Object _previousKey : stateChange.getPreviousState()) {
            Key previousKey = (Key) _previousKey;
            if(!stateChange.getNewState().contains(previousKey)) {
                ServiceTree.Node previousNode = serviceTree.getNode(previousKey);
                if(states != null) {
                    serviceTree.traverseSubtree(previousNode, ServiceTree.Walk.POST_ORDER, node -> {
                        states.remove(node.getKey().toString());
                        Log.i(TAG, "Destroy [" + node + "]");
                    });
                }
                serviceTree.removeNodeAndChildren(previousNode);
            }
        }
        for(Object _newKey : stateChange.getNewState()) {
            Key newKey = (Key) _newKey;
            buildServices(states, newKey);
        }
    }

    private void buildServices(StateBundle states, Key newKey) {
        if(!serviceTree.hasNodeWithKey(newKey)) {
            ServiceTree.Node node;
            if(newKey instanceof Child) {
                node = serviceTree.createChildNode(serviceTree.getNode(((Child) newKey).parent()), newKey);
            } else {
                node = serviceTree.createRootNode(newKey);
            }
            buildServicesForKey(states, newKey, node);
        }
    }

    private void buildServicesForKey(StateBundle states, Key newKey, ServiceTree.Node node) {
        newKey.bindServices(node);
        restoreServiceStateForKey(states, newKey, node);
        if(newKey instanceof Composite) {
            for(Object _nestedKey : ((Composite) newKey).keys()) {
                Key nestedKey = (Key) _nestedKey;
                ServiceTree.Node nestedNode = serviceTree.createChildNode(node, nestedKey);
                buildServicesForKey(states, (Key) _nestedKey, nestedNode);
            }
        }
        if(newKey.hasNestedStack()) {
            Backstack nestedStack = serviceTree.getNode(newKey).<BackstackManager>getService(Key.NESTED_STACK).getBackstack();
            for(Object _childKey : nestedStack.getInitialParameters()) {
                buildServices(states, (Key) _childKey);
            }
        }
    }

    private void restoreServiceStateForKey(StateBundle states, Key key, ServiceTree.Node node) {
        if(states != null) {
            StateBundle keyBundle = states.getParcelable(key.toString());
            if(keyBundle != null) {
                List<ServiceTree.Node.Entry> entries = node.getBoundServices();
                for(ServiceTree.Node.Entry entry : entries) {
                    if(entry.getService() instanceof Bundleable) {
                        ((Bundleable) entry.getService()).fromBundle(keyBundle.getParcelable(entry.getName()));
                    }
                }
            }
        }
    }

    public void setRestoredStates(StateBundle states) {
        serviceTree.registerRootService(SERVICE_STATES, states);
    }

    public ServiceTree getServiceTree() {
        return serviceTree;
    }

    public boolean handleBack(Context context) {
        ServiceTree serviceTree = ServiceLocator.getService(context, ServiceLocator.SERVICE_TREE);
        LinkedList<Object> keys = new LinkedList<>(serviceTree.getKeys());
        Object lastKey = keys.getLast();
        Backstack backstack = Navigator.getBackstack(context);
        class Cancellation {
            private boolean cancelled;
        }
        Cancellation cancellation = new Cancellation();
        serviceTree.traverseChain(serviceTree.getNode(lastKey), (node, cancellationToken) -> {
            if(node.getParent() == null) {
                return;
            }
            Key key = node.getKey();
            if(key.hasNestedStack()) {
                BackstackManager backstackManager = serviceTree.getNode(key).getService(Key.NESTED_STACK);
                if(backstackManager != null && backstackManager.getBackstack().goBack()) {
                    cancellation.cancelled = true;
                    cancellationToken.cancel();
                }
            }
        });
        if(cancellation.cancelled) {
            return true;
        }
        return backstack.goBack();
    }
}
