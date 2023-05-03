# SeamCarving
Java GUI application for seam carving that takes user input to assist in preserving important parts of the image.


**Filter White:**
  
  -lighter pixels get filtered out first<br>
  -brush color will become black
 
 **Filter Black:**
  
  -darker pixels get filtered out first<br>
  -brush color will become white
  
 
 **Image filtering:**
  
  -A greyscale will be applied to a temporary image that is used as a template by the seam carving algorithm.<br>
  -Three optional 3*3 kernel matrices will be applied to the image (default: identity kernel)
 
 **Brush strokes:**
  
  -scrolling in and out changes brush size, see "Filter White" for color options<br>
  -Assists algorithm to preserve parts (or remove parts) of the image 
  
 **Size to shrink image down to (%):**
  
  -This text field will allow a user to specify what percentage they want their resulting image to be based off of the original image size. 
  -example:
    
    Original width: 1000px
    Percentage: 60%;
    Resulting image width: 600px
*******************************************************************
   
**NOTICE:**

There is no way to undo brush strokes individually.              
Making an adjustment to the kernels will reset all brush strokes so add your brush strokes last.


Application does not currently have any progress indication after hitting the run button, but will resume function upon completing the carving progress at which time you can save the resulting image. Making a modification to the kernels will reset all progress so if you would like to save the resulting image do it before modifying any of the filtering tools. 
