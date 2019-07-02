package karbonator.util;

public class Machine implements Runnable {
    
    private static final int FRAMES_PER_SECOND = 60;
    private static final int NANOS_PER_FRAME = 1000000000/FRAMES_PER_SECOND;
    
    public static interface Worker {
        
        public void processFrame();
        
    }
    
    public boolean isAlive() {
        return alive;
    }
    public void terminate() {
        alive = false;
    }
    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean v) {
        running = v;
    }
    
    public void attachWorker(Worker o) {
        if(!running) {
            pWorker = o;
        }
    }
    
    public void processFrame() {
        if(pWorker != null) {
            pWorker.processFrame();
        }
    }
    @Override
    public void run() {
        for(;alive;) {
            if(running && pWorker != null) {
                ////////////////////////////////
                //Get current tick.
            
                startTick = System.nanoTime();
                
                ////////////////////////////////
                
                ////////////////////////////////
                //Execute one frame.
                
                pWorker.processFrame();
                
                ////////////////////////////////
                
                ////////////////////////////////
                //Discard rest ticks.
                
                endTick = System.nanoTime();
                elapsedTick = endTick - startTick;
                restTick = (NANOS_PER_FRAME - elapsedTick)/1000000;
                if(restTick >= 0) {
                    try {
                        Thread.sleep(restTick);
                    }
                    catch(InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                
                ////////////////////////////////
            }
            else {
                Thread.yield();
            }
        }
    }
    
    public Machine() {
        startTick = 0;
        endTick = 0;
        elapsedTick = 0;
        restTick = 0;
        alive = true;
        running = false;

        attachWorker(null);
    }
    public Machine(Worker worker) {
        startTick = 0;
        endTick = 0;
        elapsedTick = 0;
        restTick = 0;
        alive = true;
        running = false;
        
        attachWorker(worker);
    }
    
    private long startTick;
    private long endTick;
    private long elapsedTick;
    private long restTick;
    private boolean alive;
    private boolean running;
    
    private Worker pWorker;
    
}
