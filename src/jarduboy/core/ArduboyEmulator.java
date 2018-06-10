package jarduboy.core;

import javr.core.AVR;
import javr.core.AvrDecoder;
import javr.core.AvrExecutor;
import javr.core.Wire;
import javr.io.HexFile;
import javr.memory.ByteMemory;
import javr.memory.IoMemory;
import javr.memory.MultiplexedMemory;
import javr.peripherals.DotMatrixDisplay;
import javr.util.IdealWire;
import javr.util.WireArrayPort;

/**
 * Provides a complete configuration of the simple game console, where inputs
 * are generated by a simulation. The flash memory is also profiled so that we
 * can determine which instructions were accessed during the simulation. Using
 * this information, we can then determine coverage.
 *
 * @author David J. Pearce
 *
 */
public class ArduboyEmulator {
	/**
	 * The ATmega32u4 Microcontroller which underpins the TinyBoy.
	 */
	private final AVR.Instrumentable avr;
	/**
	 * Represents the dot-matrix display on the TinyBoy.
	 */
	private final DotMatrixDisplay display;
	/**
	 * Represents the four directional buttons on the TinyBoy.
	 */
	private final ControlPad pad;

	public ArduboyEmulator() {
		// Construct the micro-controller
		this.avr = constructATmega32u4();
		// Construct and connect components
		Wire[] pins = avr.getPins();
		// NOTE: we connect the display MISO and SS to LOW as they are not needed in
		// this design, thereby freeing up pins for the button pad.
		this.display = new DotMatrixDisplay(128, 64, new Wire[] { avr.getPin("SCLK"), avr.getPin("MOSI"), Wire.LOW, Wire.LOW });
		this.pad = new ControlPad(pins[3],pins[4],pins[5],pins[6]);
	}

	public AVR.Instrumentable getAVR() {
		return avr;
	}

	/**
	 * Get the width of the display in pixels.
	 *
	 * @return
	 */
	public int getDisplayWidth() {
		return 64;
	}

	/**
	 * Get the height of the display in pixels.
	 *
	 * @return
	 */
	public int getDisplayHeight() {
		return 64;
	}

	/**
	 * Check whether a given pixel is set or not.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isPixelSet(int x, int y) {
		return display.isSet(x, y);
	}

	/**
	 * Get the current state of a given button.
	 *
	 * @param button
	 * @return
	 */
	public boolean getButtonState(ControlPad.Button button) {
		return pad.getState(button);
	}

	/**
	 * Set a given button on the tiny boy.
	 *
	 * @param button
	 * @param value
	 */
	public void setButtonState(ControlPad.Button button, boolean value) {
		pad.setState(button, value);
	}

	/**
	 * Reset the TinyBoy.
	 */
	public void reset() {
		avr.reset();
		pad.reset();
		display.reset();
	}

	/**
	 * Upload a new firmware to the tiny boy. This writes the firmware to the flash
	 * memory.
	 *
	 * @param firmware
	 */
	public void upload(HexFile firmware) {
		firmware.uploadTo(avr.getCode());
	}

	/**
	 * Clock the simulation once.
	 */
	public void clock() {
		display.clock();
		pad.clock();
		avr.clock();
	}

	// PORTB Pins
	private static final int PIN_PB0 = 8;
	private static final int PIN_PB1 = 9;
	private static final int PIN_PB2 = 10;
	private static final int PIN_PB3 = 11;
	private static final int PIN_PB4 = 28;
	private static final int PIN_PB5 = 29;
	private static final int PIN_PB6 = 30;
	private static final int PIN_PB7 = 12;

	/**
	 * Construct an AVR instance representing the ATmega32u4. This needs to be
	 * instrumentable so that we can add hooks for coverage testing, etc.
	 *
	 * @return
	 */
	public AVR.Instrumentable constructATmega32u4() {
		// This is the configuration for an ATmega32u4
		final int PINB = 0x23;
		final int DDRB = 0x24;
		final int PORTB = 0x25;
		// ATmega has 44 pins.
		Wire[] pins = new Wire[44];
		pins[PIN_PB0] = new IdealWire("PB0","SS","PCINT0");
		pins[PIN_PB1] = new IdealWire("PB1","PCINT1","SCLK");
		pins[PIN_PB2] = new IdealWire("PB2","PDI","PCINT2","MOSI");
		pins[PIN_PB3] = new IdealWire("PB3","PCINT3","MISO");
		pins[PIN_PB4] = new IdealWire("PB4");
		pins[PIN_PB5] = new IdealWire("PB5");
		pins[PIN_PB6] = new IdealWire("PB6");
		pins[PIN_PB7] = new IdealWire("PB7");
		// ATmega has a single port
		WireArrayPort portb = new WireArrayPort(PORTB, DDRB, PINB, pins[PIN_PB0], pins[PIN_PB1], pins[PIN_PB2],
				pins[PIN_PB3], pins[PIN_PB4], pins[PIN_PB5], pins[PIN_PB6], pins[PIN_PB7]);
		// ATmega has 32 general purpose registers.
		AVR.Memory registers = new ByteMemory(32);
		// ATmega has 64 io registers
		AVR.Memory io = new IoMemory(new ByteMemory(64), portb);
		// ATmega has 2.5 KB of SRAM
		AVR.Memory SRAM = new ByteMemory(2560);
		// ATmega has 32K programmable flash.
		AVR.Memory flash = new ByteMemory(32768);
		// Multiplex it all together.
		AVR.Memory data = new MultiplexedMemory(registers, io, SRAM);
		//
		return new AVR.Instrumentable(new AvrDecoder(), new AvrExecutor(), pins, flash, data);
	}
}