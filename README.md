# Demonstration of UDP in Scala

There are four projects in two pairs.  

* The (Listener, Sender) pair demonstrates simplex one way communication.
* The (Client, Server) pair demonstrates duplex two way communication.

The util project simply provides some common code and dependencies.

## Getting Started

First, run <code>gradle wrapper</code>.  From this point, all gradle interaction will be through the <code>mk</code> 
script which is just a front for <code>gradlew</code> that saves some typing.

Now, run <code>mk idea</code> to generate the idea workspace and projects.  Each of the UDP endpoint projects (detailed
below) has the application plugin and so can be invoked from the command line using <code>mk</code>.

## Notes

This project suite also demonstrates how to get the gradle idea plugin to emit extra settings so that one can regenerate 
the IDEA files freely without losing settings that the plugin doesn't natively handle (esp. extra compiler settings).
   
## Simplex

### Listener

Receives UDP messages and echos them to the terminal

### Sender

Sends UDP messages intended for the listener.

## Duplex

### Client

Sends UDP messages and receives replies.

### Server

Receives UDP messages and sends replies.
