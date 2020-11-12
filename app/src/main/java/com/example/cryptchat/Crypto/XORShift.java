package com.example.cryptchat.Crypto;

public class XORShift {

    long last;

    public XORShift()
    {
        this(System.currentTimeMillis());
    }

    public XORShift(long seed) {
        last = seed;
    }

    public long nextLong(long max) {

        last ^= (last << 23);
        last ^= (last >> 18);
        last ^= (last << 5);

        long out = last % max;

        return (out < 0) ? -out : out;

    }

}
