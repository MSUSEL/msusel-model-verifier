/**
 * The MIT License (MIT)
 *
 * MSUSEL Quamoco Verifier
 * Copyright (c) 2015-2017 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.codetree.quamoco.verifier;

/**
 * A Command Line Progress Bar which will continually update the same line
 * (unless more info is added to the output).
 * 
 * @author Isaac Griffith
 * @version 1.1.1
 */
public class ProgressBar {

    /**
     * value representing the progress, in percentage, of the underlying process
     */
    private float   progress;
    /**
     * time in ms when the underlying process began
     */
    private long    start;
    /**
     * string representing the state of the bar
     */
    private String  bar;
    /**
     * time in ms that the underlying process has been computing
     */
    private long    time;
    /**
     * boolean flag indicating whether the process driving this bar has finished
     */
    private boolean finished = false;

    /**
     * Constant representing the length of the bar (in characters)
     */
    private static final int    LENGTH = 60;
    /**
     * Constant defining the format of the output line
     */
    private static final String FORMAT = "[%s] %6.2f%% %dms";

    /**
     * Test main method
     * 
     * @param args
     *            command line arguments
     */
    public static void main(String[] args)
    {
        ProgressBar pb = new ProgressBar();
        pb.start();
        for (int i = 0; i < 100; i += 25)
        {
            try
            {
                Thread.sleep(1500);
            }
            catch (InterruptedException e)
            {

            }

            pb.tick(25);
        }
        pb.finish();
    }

    /**
     * Constructs a new progress bar.
     */
    public ProgressBar()
    {
        progress = 0;
    }

    /**
     * Start the time tracking and initialize the bar at 0%
     */
    public void start()
    {
        start = System.currentTimeMillis();
        progress = 0;
        tick(0);
    }

    /**
     * End time tracking and set progress to 100%
     */
    public void finish()
    {
        if (!finished)
        {
            finished = true;
            tick(100 - progress);
        }
    }

    /**
     * Increments progress by 1%
     */
    public void tick()
    {
        tick(1);
    }

    /**
     * Increments progress by amount provided (in percentage points).
     * 
     * @param amount
     *            Percentage to increment progress by.
     */
    public void tick(float amount)
    {
        time = System.currentTimeMillis() - start;
        progress += amount;

        buildBar();
        update();
    }

    /**
     * Updates the display.
     */
    private void update()
    {
        System.out.print(String.format(FORMAT, bar, progress, time));
        if (!finished)
            System.out.print("\r");
        else
            System.out.print("\n");
    }

    /**
     * Constructs the bar.
     */
    private void buildBar()
    {
        StringBuilder b = new StringBuilder();
        int barLength = (int) ((progress / 100) * LENGTH);
        for (int i = 0; i < barLength; i++)
            b.append('=');
        for (int i = 0; i < LENGTH - barLength; i++)
            b.append(' ');
        bar = b.toString();
    }
}
