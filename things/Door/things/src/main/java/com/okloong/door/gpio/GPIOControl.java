package com.okloong.door.gpio;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.Pwm;
import com.google.android.things.pio.SpiDevice;
import com.google.android.things.pio.UartDevice;

import java.io.IOException;
import java.util.List;

/**
 * Created by loongago on 2018-03-07.
 */

public interface GPIOControl {

    public List<String> getGpioList();

    public List<String> getPwmList();

    public List<String> getSpiBusList();

    public List<String> getI2cBusList();

    public List<String> getUartDeviceList();

    public Gpio openGpio(String name) throws IOException;

    public Pwm openPwm(String name) throws IOException;

    public SpiDevice openSpiDevice(String name) throws IOException ;

    public I2cDevice openI2cDevice(String name, int address) throws IOException;

    public UartDevice openUartDevice(String name) throws IOException;
}
