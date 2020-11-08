## Smart image loading demo

### Assignment
So, the assignment is to implement a camera roll making the image loading quick and ideally
completely transparent (users don’t like loading…) and having a smooth and junk-free scrolling.
It's not 100% explicit what transparent means in this case, I guess we try not make the user
aware it's actually happening.

Of course I've used a number libraries for this in the past, they all do something like loading on a
background thread and caching results. But I think for a good UX we need to also add pre fetching
of images the user will likely scroll to.

### Implementation

The basis to showcase this will just have a simple fragment with a recyclerview and a Groupie adapter,
and a viewModel that gets the "photo" data from the `PhotoRepository`

For the pre-loading / caching implementation I'll pretend that it would end up in production code
(although I won't try to make it perfect of course, the target was about 8 hours of work i heard)
so we want to adhere to the general principles of single-responsibility, loose-coupling, and flexibility.
We want to have it separate from the adapter so we can reuse it.

The nicest interface would be have one object `SmartImageLoader` with method `getImage(url: String)`
that returns the image asynchronously either from the web or the cache, but we need to do a check
after the result is available to see if it's still wanted in that ImageView! In order to keep usage
simple we put that logic in the image loader, therefore we have a public method
`fun load(url: String, imageView: ImageView)` that will change the state of the imageView.

This `SmartImageLoader` consists of:
- an `ImageLoader` that actually downloads the image from a url on a IO thread
- a cache where loaded images are retained
    - I only used a memory cache here, adding a disk cache is trivial, and for this small data set
     would actually feel a bit like cheating
- a download queue that can limit concurrent API requests
    - this is not strictly needed, but can be useful to limit impact on the back-end. In real life I
    would check with back end people what is needed here...
- a pre load strategy that will start downloading images before they are needed.

This pre load strategy is what will actually make sure the user doesn't see any loading.
For this it needs access to the adapter because it needs to know what images might be needed soon.
It can also listen to fling events, because if a user flings very fast
we wont be able to preload everything on time and when the fling stops we don't want to wait with
showing the images on screen before all the images that are off screen are loaded.

By the way, given this data set it would be possible to just preload all images
(500 128x128 images is just 8MB) but that would be cheating 😁 So we'll set a smaller size for the cache.

Basically, the only challenging part of this is to make sure that we handle the scenario where an
image is needed on screen for which a pre-load task is already started but not completed well.
When images are being pre-loaded, the program doesn't know yet in which view it will end up.
So when an image view asks the `SmartImageLoader` for an image, it could be that request for that
image is being executed or is in the queue for execution.

The `SmartImageLoader` takes care of this without any code on the caller side. It is done by using
`Deferred<Bitmap>` for the queue and the cache.

### Final thoughts

The provided solution has several parameters that can be tweaked:
- cache size
- concurrent API call limit
- pre-fetch size
- how to handle flinging

What the right settings are depends on used image resolution and should be tested on multiple phones.
