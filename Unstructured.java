/*
1)	Dining Philosopher
Complete the in class assignment in which you create 5 Philosophers eating spaghetti at a table with 5 forks. 

A,B,C,D and E represent the 5 Philosophers
The philosophers can do the following:
EAT if they have 2 forks.  Random number 1-10 sec
NOTE: forks must be to their immediate left and right
THINK Random number 1-5 sec

Implement the problem above with 5 philosopher threads and locks
a)	Use structured locks // synchronized
b)	Use unstructured locks // reentrantLocks

At all times the philosophers should be printing there console to action:
Philosopher A: attempt to acquire forks to left
Philosopher A: acquired left forks
Philosopher A: attempt to acquire forks to right
Philosopher A: acquired right forks
Philosopher A: Eating for 3 seconds
Philosopher A: Thinking for 4 seconds
 */
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author Jamie
 */
public class Unstructured
{
    static ReentrantLock lock = new ReentrantLock();
    static Random random = new Random(); // used in Philosopher class
    static Philosopher[] philosophers = new Philosopher[5];
    static boolean[] forks = new boolean[5]; // false means not picked up. 
    public static void main(String[] args) throws InterruptedException
    {
        initializePhilosophers();
        
        long maxTime = 20000;
        long endTime;
        System.out.println("run for " + maxTime/1000 + " second(s)");
        long startTime = System.currentTimeMillis();
        for ( int i = 0; i < philosophers.length; i++ )
        {
            Thread thread = new Thread(new PhilosopherThread(i));
            thread.start();
        }
        for ( ; ; )
        {
            Thread.sleep(1000);
            endTime = System.currentTimeMillis();
            if ( endTime - startTime > maxTime ) break;
        }
        System.out.println(maxTime/1000 + " second(s) has passed. good bye");
        System.exit(0);
    }
    
    public static void initializePhilosophers()
    {
        for ( int i = 0; i < philosophers.length; i++ ) // initialize philosophers
        {
            if ( i == 4 )
                philosophers[i] = new Philosopher(String.valueOf((char)('A' + i)), i, 0);
            else philosophers[i] = new Philosopher(String.valueOf((char)('A' + i)), i, i+1);
        }
    }
    
    static class PhilosopherThread implements Runnable
    {
        int index;
        public PhilosopherThread(int index)
        {
            this.index = index;
        }
        
        public void run()
        {
            Philosopher p = philosophers[index];
            
            try
            {
                for ( ; ; )
                {
                    if ( p.pickupLeft() )
                    {
                        if ( p.pickupRight() )
                        {
                            p.eat();
                        }
                        else p.think();
                    }
                    else p.think();
                }
            }
            catch (InterruptedException e )
            {
            }
        }
    }
    
    static class Philosopher
    {
        String name;
        int left, right; // hold forks's index
        boolean leftForkObtained, rightForkObtained;
        public Philosopher(String name, int left, int right)
        {
            this.name = "Philosopher " + name;
            this.left = left;
            this.right = right;
            leftForkObtained = false;
            rightForkObtained = false;
        }
        
        public void think() throws InterruptedException
        {
            lock.lock();
            forks[left] = false;
            forks[right] = false;
            lock.unlock();
            int thinkTime = random.nextInt(5) + 1;
            System.out.println(name + ": is thinking for " + thinkTime + " second(s).");
            Thread.sleep(thinkTime*1000);
        }
        
        public boolean pickupLeft()
        {
            if ( leftForkObtained ) return true;
            lock.lock();
            System.out.println(name + ": attempt to acquire left fork.");
            if ( !forks[left] ) 
            {
                forks[left] = true;
                leftForkObtained = true;
                System.out.println(name + ": acquired left fork.");
            }
            lock.unlock();
            return leftForkObtained;
        }
        
        public boolean pickupRight()
        {
            if ( rightForkObtained ) return true;
            lock.lock();
            System.out.println(name + ": attempt to acquire right fork.");
            if ( !forks[right] )
            {
                forks[right] = true;
                rightForkObtained = true;
                System.out.println(name + ": acquired right fork.");
            }
            lock.unlock();
            return rightForkObtained;
        }
        
        public void eat() throws InterruptedException
        {
            if ( rightForkObtained && leftForkObtained )
            {
                int thinkTime = random.nextInt(10) + 1;
                System.out.println(name + ": is eating for " + thinkTime + " second(s).");
                Thread.sleep(thinkTime*1000);
                lock.lock();
                rightForkObtained = false;
                leftForkObtained = false;
                forks[left] = false;
                forks[right] = false;
                lock.unlock();
            }
        }
    }
}
