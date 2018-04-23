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

public class GPIOControlimpl implements GPIOControl {
    @Override
    public List<String> getGpioList() {
        return null;
    }

    @Override
    public List<String> getPwmList() {
        return null;
    }

    @Override
    public List<String> getSpiBusList() {
        return null;
    }

    @Override
    public List<String> getI2cBusList() {
        return null;
    }

    @Override
    public List<String> getUartDeviceList() {
        return null;
    }

    @Override
    public Gpio openGpio(String name) throws IOException {
        return null;
    }

    @Override
    public Pwm openPwm(String name) throws IOException {
        return null;
    }

    @Override
    public SpiDevice openSpiDevice(String name) throws IOException {
        return null;
    }

    @Override
    public I2cDevice openI2cDevice(String name, int address) throws IOException {
        return null;
    }

    @Override
    public UartDevice openUartDevice(String name) throws IOException {
        return null;
    }
}
