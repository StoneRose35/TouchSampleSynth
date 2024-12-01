# TouchSampleSynth
Android Sampler/Synth with a configurable Touch Interface

# How to add a new Instrument (mostly structural guideline)
## Concept around JNI 
Each Instrument consists of definite set of Files or Classes with different purposes. 
Generally speaking an instrument consists of one to many voices which share common settings but might be switched on or off individually.
The static of an instrument can be persisted. The static state consists of the knob settings but do not represent things such as which voice is sounding right now or not.
As C++ sources an Instrument, actually a voice is represented by a C++ class inheriting from MusicalSoundGenerator. 
Beside the class in a separate C file the jni functions are defined which allow a transparent and non-redundant map of a kotlin class to the C++ class.
The matching kotlin class is named the "K"-File residing in ch.sr35.touchsamplesynth.audio.voices, it represents a single voice of an instrument. 
The Instrument itself is represented by an "I"-File residing in ch.sr35.touchsamplesynth.audio.instruments. It contains wrappers to set or get parameters simultaneously for alle voices.
The persistable information of an instrument is defined in an "P"-File residing in ch.sr35.touchsamplesynth.model.

## Procedure
Be creative and work of a good concept for a simple electronic instrument being able to be played expressively on a tablet. Put on good music faithful to the instrument you're
creating and your current mood. Then proceed with coding.

* define a name for the Instrument
* in app/src/main/cpp/ add a C header file name after the instrument 
* create a C++ class name after the instrument which stems from MusicalSoundGenerator, add class members to taste
* for each parameter which should be editable and saveable define a getter and a setter. 
  * Getters return the type in case of primitives and the number of elements returned in case of arrays/pointers to primitive. Getter take no arguments for primitives and the pointer to the array for arrays.They follow the naming scheme "get" followed by the parameter in CamelCase
  * Setter return void and take the type as argument in case of primitives. In case of arrays a setter takes the array and the array length as a argument. They follow the naming scheme "set" followed by the parameter in CamelCase
* declare the following overrides, some might not be needed in special setups such as setNote for non-pitch based instruments
```cpp
 float getNextSample() override;
  void setNote(float note) override;
  void switchOn(uint8_t) override;
  void switchOff(uint8_t) override;
  void trigger(uint8_t) override;
  int getType() override;
  bool isSounding() override;
```
* implement the NDK/C++ Part of the instrument
* design an icon for your instrument modifying newinstrument.svg, then generate a vector asset from it
* build the project to generate the necessary classes to integrate your instrument within TouchSampleSynth
* add instrument specific kotlin code to the ```ch.sr35.touchsamplesynth.audio.instruments.<yournewinstrument>I``` if required
* implement a layout for the instrument and a Fragment to handle the UI of the instrument

* Test and Debug your Instrument
* finally, take your time to learn a little song, beat or melody using the TSS extension you crafted!