# Animated Solitaries Library

This Android Library allows you to create a solitaries animations loop. A solitary animation is an animation that the robot 
runs when it's alone to attract people.

With this library, you can also define at which frequency you want the animations to run. By default, this value is set 
to 60 seconds. **We advise you to not set this value below 30 seconds** or it may result in complains about robot being noisy 
and impossible to reach.

The solitaries animation loop can then be started and stopped whenever you want.

## Getting Started


### Prerequisites

A robotified project for Pepper with QiSDK. Read the [documentation](https://developer.softbankrobotics.com/pepper-qisdk) if needed.

### Running the Sample Application

The project comes complete with a sample project. You can clone the repository, open it in Android Studio, and run 
this directly onto a robot.

The sample demonstrate how to use the library in a project and what to do to avoid solitaries animations interacting 
with your animations.

Full implementation details are available to see in the project.

### Installing

[**Follow these instructions**](https://jitpack.io/#softbankrobotics-labs/solitaries-loop)

Make sure to replace 'Tag' by the number of the version of the library you want to use.


## Usage

*This README assumes some standard setup can be done by the user, such as initialising variables or implementing code in the correct functions. Refer to the Sample Project for full usage code.*

Initialise the QiSDK in the onCreate. If you are unsure how to do this, refer to the QiSDK tutorials [here](https://qisdk.softbankrobotics.com/sdk/doc/pepper-sdk/ch1_gettingstarted/starting_project.html)
```
QiSDK.register(this, this)
```
In the `onRobotFocusGained`, instantiate a `AnimatedSolitary` object by passing it the QiContext and start the loop:
```
animatedSolitary = AnimatedSolitary(qiContext)
```
You can also define the frequency at which you want the animations to run (time in seconds):
```
animatedSolitary = AnimatedSolitary(qiContext, 40)
```
Start the solitaries loop whenever you want (for instance if no human has been detected around the robot for X minutes):
```
animatedSolitary.start()
```
Stop the solitaries loop whenever you want (for instance if a human is engaged or if you loose the robot focus):
```
animatedSolitary.stop()
```


## License

This project is licensed under the BSD 3-Clause "New" or "Revised" License. See the [LICENSE](LICENSE.md) file for details.