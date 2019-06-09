ImageStyler
It's a semester project for Object-Oriented Programming course on Theoretical Computer Science department of Jagiellonian University.

Application allows one to stylize images using our best style images (e.g. 'The Scream' by Van Gogh) with one of several neural nets 
and maintain an own gallery of generated images.

Usage:
1) After executing an app you will find yourself at LogIn page, so register firstly.
2) At main page, you can look up your gallery, delete existing images, sort them by name or date;
in the toolbar, there are a few important buttons:
  a) Settings: here you can clean local cache (all previously downloaded from server images), change the password 
  in the Profile Settings section and log out from your account.
  b) +: it is where the things are happening - this button will lead you to the Generator page, which we will discuss later.
  c) Hide/Show: this allows you to hide/show the left bar (images list) and leave just the photo you've chosen.
  d) Neural net: here you can choose the neural net which will be used for stylizing the image.
  e) the last thing, with the bottom 'Delete' button, you can delete any image (already downloaded from server/generated).
"IN PROGRESS" image means that it is being downloaded from server or generated at that moment.

Generator page:
1) Just click on the left image to choose the image you want to stylize.
2) Click the left and right arrows under the right image to choose the appropriate style image (at the moment, you can choose from 6 images).
3) Under the right image, decide whether you would like to preserve the original size of image or 
get a generated image of size 224px (available only with SQUEEZENET and VGG16 nets).
4) Use the slider at the top to adjust how much the style will impact the generation process (available only with SQUEEZENET and VGG16 nets).
5) Name the photo below and that's it.

Difference between neural nets:
1) SqueezeNet (that one will spend around 30 secs of your time and give you average-quality (in my opinion) images).
2) VGG16 (that one will spend around 30 mins of your time and result in beautiful images).
3) Transformer (that one will spend just 10 secs of your time and generate excellent images, though 
the algorithm we used here differs absolutely from the previous ones) - beta version (appeared only a few days ago).

You cannot be worried about your information as we use RSA algorithm to encrypt all the data that streams to and from the server.
Also, we fully synchronize the changes you do from your computer with other computers that are logged in the same account - e.g. generated image on other computer on your account will appear also in your gallery in your computer.
