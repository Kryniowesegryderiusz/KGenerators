package me.kryniowesegryderiusz.kgenerators.generators.schedules.objects;

import java.time.Instant;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;

public class Schedule {

	@Getter private int timeLeft;
	
	public Schedule(int timeLeft, int scheduleSavedTimeInSeconds) {
		this.timeLeft = timeLeft;
		
		if (Main.getSettings().isAdjustDelayOnUnloadedChunks()) {
			this.timeLeft = (int) (this.timeLeft - ((Instant.now().getEpochSecond() - scheduleSavedTimeInSeconds) * 20));
		}
	}
	
	public Schedule(int timeLeft) {
		this.timeLeft = timeLeft;	
	}
	
	public void decreaseDelay(int ticks) {
		this.timeLeft-=ticks;
	}
	
	public boolean isReadyForRegeneration() {
		return this.timeLeft <= 0;
	}
	
	public String toString() {
		return "Time left: " + timeLeft;
	}
	
}
