package karbonator.util;

import karbonator.collection.Vector;

public class StateMachine {

    public static interface HState {
    
        public void onEntering(StateMachine stateMachine, Object parameter);
        public void onRunning(StateMachine stateMachine);
        public void onExiting(StateMachine stateMachine);
    
    }
    
    public void attachStateTable(Vector<HState> stateTable) {
        if(stateTable == null) {
            throw new NullPointerException("Parameter 'stateTable' should not be null.");
        }
        
        this.stateTable = stateTable;
    }
    
    public int getCurrentStateId() {
        return stateId;
    }
    public void changeState(int nextStateId, Object parameter, boolean restart) {
        ///*
        this.nextStateId = nextStateId;
        nextStateParameter = parameter;
        restartFlag = restart;
        //*/
        
        /*
        if((nextStateId != stateId) || restart) {
            state.onExiting(this);
            
            stateId = nextStateId;
            state = stateTable.at(stateId);
            
            state.onEntering(this, parameter);
        }
        */
    }
    
    public void runState() {
        /*
        state.onRunning(this);
        */
    
        ///*
        if((nextStateId != stateId) || restartFlag) {
            state.onExiting(this);
            
            stateId = nextStateId;
            state = stateTable.at(stateId);
            restartFlag = false;
            
            state.onEntering(this, nextStateParameter);
            nextStateParameter = null;
        }
        
        state.onRunning(this);
        //*/
    }
    
    public StateMachine(Vector<HState> stateTable, int initialStateId) {
        attachStateTable(stateTable);

        stateId = initialStateId;
        state = stateTable.at(stateId);
        
        ///*
        nextStateId = initialStateId;
        nextStateParameter = null;
        restartFlag = true;
        //*/
    }
    
    private Vector<HState> stateTable;

    private int stateId;
    private HState state;
    
    ///*
    private int nextStateId;
    private Object nextStateParameter;
    private boolean restartFlag;
    //*/

}
