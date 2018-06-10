package jarduboy;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jarduboy.core.ArduboyEmulator;
import jarduboy.views.ArduboyPeripheral;
import javr.core.AVR;
import javr.core.AvrDecoder;
import javr.core.AvrExecutor;
import javr.core.Wire;
import javr.io.HexFile;
import javr.memory.ByteMemory;
import javr.memory.IoMemory;
import javr.memory.MultiplexedMemory;
import javr.peripherals.DotMatrixDisplay;
import javr.util.WireArrayPort;
import javrsim.peripherals.ConsolePeripheral;
import javrsim.peripherals.DisplayPeripheral;
import javrsim.peripherals.JPeripheral;
import javrsim.views.CodeView;
import javrsim.views.DataView;
import javrsim.views.JAvrView;
import javrsim.windows.SimulationWindow;

/**
 * A simple tool for generate test coverage information for the AVR simulator.
 *
 * @author David J. Pearce
 *
 */
public class Main {
	/**
	 * The default set of peripherals.
	 */
	public static JPeripheral.Descriptor[] PERIPHERALS = {

	};
	/**
	 * The default set of views.
	 */
	public static JAvrView.Descriptor[] VIEWS = {
			CodeView.DESCRIPTOR,
			DataView.DESCRIPTOR
	};
	public static void main(String[] args) throws IOException {
		// Construct the tinyBoy emulator
		ArduboyEmulator tinyBoy = new ArduboyEmulator();
		// Construct the main simulation window
		SimulationWindow sim = new SimulationWindow(tinyBoy.getAVR(), PERIPHERALS, VIEWS);
		// Finally, construct the TinyBoy view
		JPeripheral p = new ArduboyPeripheral(tinyBoy);
		sim.addPeripheral(p);
	}

}
