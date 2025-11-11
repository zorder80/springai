package com.flavio.springai.tools;

import org.springframework.ai.tool.annotation.Tool;

public class DucatiTool {

    @Tool(description = "Provides information about world ducati week 2026")
    public String getWDW2026Info() {
         return "world ducati week 2026 will be held in Misano Adriatico, Italy from July 3 to July 5, 2026. " +
                 "It is a gathering event for Ducati enthusiasts featuring various activities, exhibitions, and rides.";
    }

    @Tool(description = "Provides information about ducati panigale v4 2026 model")
    public String getPanigaleV42026() {
        return "Panigale V4 2026, 216 cv, 187 kg. The new Panigale V4 is the motorcycle that comes closest to the MotoGP, " +
                "inheriting some of the technical solutions developed by the multi-world title winning " +
                "team so as to offer the rider the same riding sensations as professional riders.";
    }
}
