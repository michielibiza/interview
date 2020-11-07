## Smart image loading demo

So, the assignment is to implement a camera roll where pre-loading happens automatically,
ideally without the user noticing any loading. The basis to showcase this will just have a simple
fragment with a recyclerview and a Groupie adapter, and a viewModel that gets the "photo" data
from the `PhotoRepository`

For the pre-loading / caching implementation I'll pretend that it would end up in production code
(although I won't try to make it perfect of course, the target was about 8 hours of work i heard)
so we want to adhere to the general principles of single-responsibility, loose-coupling, and flexibility.
We want to have it separate from the adapter so we can reuse it,
the nicest interface is to have one object `SmartImageLoader` with method `getImage(url: String)`
that returns the image asynchronously either from the web or the cache.
This `SmartImageLoader` consists of:
- an `ImageLoader` that actually downloads the image from a url
- a cache where loaded images are retained
- a download queue that can throttle API requests
- a pre load strategy that will start downloading images before they are needed.

This pre load strategy is what will actually make sure the user doesn't see any loading.
For this it needs access to the adapter because it needs to know what images might be needed soon.
It can also listen to fling events, because if a user flings very fast
we wont be able to preload everything on time and when the fling stops we don't want to wait with
showing the photos on screen before all the photos that are off screen are loaded.

By the way, given this data set it would be possible to just preload all images
(500 128x128 images is just 8MB) but that would be cheating 😁 So we'll set a smaller size for the cache.

Basically, the only challenging part of this is to make sure that we handle the scenario where an
image is needed on screen for which a pre-load task is already started but not completed well.
When images are being pre loaded, the program doesn't know yet in which view it will end up.
So when an image view asks the `SmartImageLoader` for an image, it could be that request for that
image is being executed or is in the queue for execution.
The `SmartImageLoader` should take of this without any code on the caller side.

