package com.github.lolopasdugato.mcwarclan;

/**
 * Created by Seb on 11/04/2014.
 */
public class Exception extends Throwable {

    static public class NotValidFlagLocationException extends Exception {
    }

    static public class NotEnoughSpaceException extends Exception {
    }

    static public class NoPlayerToKickException extends Exception{
    }

    static public class NoBarbarianTeamException extends Exception{

    }

    static public class AddTeamArrayException extends Exception{

    }

    static public class MaximumTeamCapacityException extends Exception{

    }
}
