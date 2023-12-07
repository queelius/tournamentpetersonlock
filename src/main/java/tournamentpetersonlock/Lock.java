package tournamentpetersonlock;

public interface Lock
{
	public void lock();   // before entering critical section
	public void unlock(); // before leaving critical section
}
