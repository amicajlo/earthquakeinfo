***** EarthquakeInfo app *****

* Developer: Hai Dang

* Time spent: 14 hours

* Project type: Eclipse (ADT 23.0.2)

* Supported Android version: 4.1+

* Projects included: 
			
				- EarthquakeInfo (app project)
				- EarthquakeInfoTest (test project)
				- MPChartLib (lib project for charts)
				- google-play-services_lib (Google play service lib)
				- Volley (Google lib for fast networking)
				

* Libraries used:

				- Jackson library (for JSON parsing) - http://jackson.codehaus.org/
				- Android Volley network lib - http://developer.android.com/training/volley/index.html
				- MPChart Graph/chart library - https://github.com/PhilJay/MPAndroidChart
				
* Keystore: 

				- keystore/earthquake_keystore
				- password: haidang123
				- key password: haidang123
				
* Production build:

				- build_export/EarthquakeInfo-release.apk
				
* App Design:

       + UI: standard Android design using Action Bar, Navigation Drawer and Fragments.
       
       + Architecture Design:
       				
       					- Database: data is persisted in database and is retrieved via ContentProvider by CursorLoader
       					- Each main app feature screen is implemented using Fragment. State of each fragment is preserved by HomeScreen.
       					- Volley lib provide a clean way to achieve fast and reliable networking requests.
       					
       	+ Reusability:
       					- Network classes and some utility classes are implemented with reusability in mind so they can be generic enough to plug in any other
       					Android projects with minimum changes needed to be made.
				
				