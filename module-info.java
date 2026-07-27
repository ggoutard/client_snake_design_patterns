/**
 * 
 */
/**
 * 
 */

module tp1progreseau {
	requires com.fasterxml.jackson.databind;
	requires java.desktop;
	requires com.fasterxml.jackson.annotation;
	opens tp1progreseau.utils to com.fasterxml.jackson.databind;
}

