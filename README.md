inspector-mzgraph-browser
===============

# About Inspector mzgraph browser

The purpose of PRIDE Inspector mzgraph browser library is to visualize and annotate MS spectrum and chromatogram.

# License

inspector-mzgraph-browser is a PRIDE API licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

# How to cite it:

Wang, R., Fabregat, A., Ríos, D., Ovelleiro, D., Foster, J. M., Côté, R. G., ... & Vizcaíno, J. A. (2012). PRIDE Inspector: a tool to visualize and validate MS proteomics data. Nature biotechnology, 30(2), 135-137. [PDF File](http://www.nature.com/nbt/journal/v30/n2/pdf/nbt.2112.pdf), [Pubmed Record](http://www.ncbi.nlm.nih.gov/pubmed/22318026)

# Main Features

* Zoom in/out.
* Export peak values.
* Save/Print spectrum and chromatogram as image.
* Highlight peak m/z and intensity values.
* Highlight mass differences.
* Display fragment ion annotations.
* Automatic annotation of amino acid identifications.
* Filtering on ion series.
* Filtering on annotation series

We believe that this library is both easy to learn and to extend. It can be of great use for developing computational proteomics tools.

This library is developed using Java, it uses both jFreeChart and PRIDE utilities library extensively.

# Getting Inspector mzgraph browser

The zip file in the releases section contains the PRIDE Inspector mzgraph browser jar file and all other required libraries.

Maven Dependency

PRIDE Inspector Quality Chart library can be used in Maven projects, you can include the following snippets in your Maven pom file.
 
 ```maven
 <dependency>
   <groupId>uk.ac.ebi.pride.toolsuite</groupId>
   <artifactId>inspector-mzgraph-browser</artifactId>
   <version>2.0.0-SNAPSHOT</version>
 </dependency> 
 ```
 ```maven
 <!-- EBI repo -->
 <repository>
     <id>nexus-ebi-repo</id>
     <url>http://www.ebi.ac.uk/intact/maven/nexus/content/repositories/ebi-repo</url>
 </repository>
 
 <!-- EBI SNAPSHOT repo -->
 <snapshotRepository>
    <id>nexus-ebi-repo-snapshots</id>
    <url>http://www.ebi.ac.uk/intact/maven/nexus/content/repositories/ebi-repo-snapshots</url>
 </snapshotRepository>
```
Note: you need to change the version number to the latest version.

For developers, the latest source code is available from our SVN repository.

# Getting Help

If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: pride-support at ebi.ac.uk (replace at with @).

Please send us your feedback, including error reports, improvement suggestions, new feature requests and any other things you might want to suggest to the PRIDE team.

# This library has been used in:

* Wang, R., Fabregat, A., Ríos, D., Ovelleiro, D., Foster, J. M., Côté, R. G., ... & Vizcaíno, J. A. (2012). PRIDE Inspector: a tool to visualize and validate MS proteomics data. Nature biotechnology, 30(2), 135-137. [PDF File](http://www.nature.com/nbt/journal/v30/n2/pdf/nbt.2112.pdf), [Pubmed Record](http://www.ncbi.nlm.nih.gov/pubmed/22318026)
* Perez-Riverol, Yasset, Aniel Sánchez, Jesus Noda, Diogo Borges, Paulo Costa Carvalho, Rui Wang, Juan Antonio Vizcaíno et al. "HI-Bone: A Scoring System for Identifying Phenylisothiocyanate-Derivatized Peptides Based on Precursor Mass and High Intensity Fragment Ions." Analytical chemistry 85, no. 7 (2013): 3515-3520.
* Vizcaíno, J. A., Côté, R. G., Csordas, A., Dianes, J. A., Fabregat, A., Foster, J. M., ... & Hermjakob, H. (2013). The PRoteomics IDEntifications (PRIDE) database and associated tools: status in 2013. Nucleic acids research, 41(D1), D1063-D1069. [PRIDE-Archive](http://www.ebi.ac.uk/pride/archive/)

How to use inspector-mzgraph-browser
===============

# Using Inspector mzgraph browser 

PRIDE mzGraph Browser library gives you Java Swing based components for visualizing and annotating MS spectra and chromatogram.

This library is designed to be integrated into your project easily, and there are two common ways of using it:

As an independent panel: use this option if you just want to visulize spectrum or chromatogram or you want to program the user interactions yourself.
As a panel with a build-in tool bar: this option provides you with the main visualize panel, in addition, you will also get a out-box tool bar which can be customized to include your own actions.

# Spectrum Panel

The starting point in using PRIDE mzGraph library to visualize spectrum is to create a SpectrumPanel. This panel can be added as a component to Java Swing container, and it is the basis of all user interactions.

The following code shows you how to create an instance of the SpectrumPanel and add it to the Swing component in your project:

```java
// Create a m/z data array
double[] mzArr = new double[]{1.0, 2.012312313, 3.0, 4.234, 6.0, 7.34342};
// Create an intensity data array
double[] intentArr = new double[]{2.0, 4.345345345, 6.0, 1.4545, 5.0, 8.23423};
// Create a spectrum panel
SpectrumPanel spectrum = new SpectrumPanel(mzArr, intentArr);
// Paint the spectrum peaks
spectrum.paintGraph();
// Added the spectrum panel to your own JPanel
JPanel container = new JPanel(new BorderLayout());
container.add(spectrum, BorderLayout.CENTER);
```

Only an array of m/z values and an array of intensity values are required to build a spectrum. You can also overwrite this peak list with a new one, the code below shows you how:

```java
// New m/z array
double[] newMz = new double[]{2.0, 3.0, 12.23, 1.45};
// New intensity array
double[] newIntent = new double[]{45, 67, 18.34, 34.78};
// Set a new peak list
spectrum.setPeakList(newMz, newIntent);
```

After the spectrum has been initialized, you can annotate it with fragment ion information. Below is an example of adding a b ion and a y ion:

```java
// Create a new y ion with charge -2 and location 2 as well as a water loss
IonAnnotationInfo yIonInfo = new IonAnnotationInfo();
// Create and add an annotation item which describes the ion.
IonAnnotationInfo.Item yIonItem = new IonAnnotationInfo.Item(-2, FragmentIonType.Y_ION, 2, NeutralLoss.WATER_LOSS);
yIonInfo.addItem(yIonItem);
// Create the y ion
IonAnnotation yIon = new IonAnnotation(2.0, 45, yIonInfo);

// Create a new b ion with charge +1 and location 3
IonAnnotationInfo bIonInfo = new IonAnnotationInfo();
IonAnnotationInfo.Item bIonItem = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 3, null);
bIonInfo.addItem(bIonItem);
IonAnnotation bIon = new IonAnnotation(12.23, 18.34, bIonInfo);

// Add these ions to the spectrum
List<IonAnnotation> ions = new ArrayList<IonAnnotation>();
ions.add(yIon);
ions.add(bIon);
spectrum.addFragmentIons(ions);
```

If several ions of the same series are added, SpectrumPanel will try to assign amino acid annotations between the ion peaks. However, if the identified peptide has post translational modifications(PTM), you will need to let SpectrumPanel know the length of the peptide as well as the modification details. The code below shows you how, assuming the length of the peptide is 8 and a list of PTMs are stored in modifications:

```java
// Set the length of peptide and PTMs as annotation parameters
spectrum.setAminoAcidAnnotationParameters(8, modifications);
spectrum.addFragmentIons(ions);
```

#Spectrum Panel with a Build-in Tool Bar

SpectrumBrowser is an extension of SpectrumPanel, it uses SpectrumPanel to visualize spectrum, it also added a expandible tool bar to perform some common actions. For instance, save/print spectrum as an image, hide the entire peak list and clear all the highlighted mass differences. More importantly, it provides a annotation panel which can filter based on ion series and amino acid annotation series.

You can also add your own component or actions to the build-in tool bar. For instance, if you have a general description panel related to a spectrum, and you would like to add it to the tool bar, the code below shows you how:

```java
// Create a new SpectrumBrowser
SpectrumBrowser browser = new SpectrumBrowser();
// Set the spectrum peak list
browser.setPeakList(mzArray, intensityArray);
// Add fragment ions
browser.addFragmentIons(ions);

// Create a general description panel
JPanel descPanel = new JPanel();
// Add the panel to SpectrumBrowser
browser.add(icon, label, tooltip, actionCommand, descPanel);
```
This code will add a new button the tool bar, the general description panel will show/hide when you click on the button.
