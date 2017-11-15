# EMF Exposure
Leandro Carísio Fernandes

This app is described in detail in "The Design and Development of an App to Compute Exposure to Electromagnetic Fields [EM Programmer's Notebook]", published in IEEE Antennas and Propagation Magazine, Vol. 59, Issue 2, 2017 (DOI: 10.1109/MAP.2017.2655527).

## Disclaimer

This open-source implementation tool is provided \"AS IS\" with no warranties, express or implied, including but not limited to, the warranties of merchantability, fitness for a particullar purpose and non-infringment of intellectual property rights and neither the Developer (or its affiliates) nor the ITU shall be held liable in any event for any damages whatsover (including, without limitation, damages for loss of profits, business interruption, loss of information, or any other pecuniary loss) arising out of or related to the use of or inability to use this tool.

The evaluations are based on conservative models and may overestimate real exposure.

## Using the source code

To use the source code, follow the steps:

1) Create a directory and clone the repository in it:

```
mkdir emf-exposure
git clone https://github.com/carisio/emf-exposure.git emf-exposure
```

2) Open Android Studio and select "Import Project (Gradle, Eclipse ADT, etc.)". Point to the directory cloned.

Android Studio will import the project and try to build it. Depending on what you have installed, it will show an error message with the link "Install missing ... and sync project". Just click que link and install what it is missing to build the project.

3) This project uses the Google Maps API. To use it, you will need to enable it in your Google Developer Console. Just enable the API in your console and link it to your account (do not forget to edit AndroidManifest.xml file and change the key to your key).