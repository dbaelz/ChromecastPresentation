var cast = window.cast || {};

(function() {
    'use strict';

    var images = ["slide1.png", "slide2.png", "slide3.png"];
    var currentImageIndex = null;

    function Presentation() {
        this.mChannelHandler = new cast.receiver.ChannelHandler('PresentationDebug');
        this.mChannelHandler.addEventListener(cast.receiver.Channel.EventType.MESSAGE, this.onMessage.bind(this));
        this.mChannelHandler.addEventListener(cast.receiver.Channel.EventType.OPEN, this.onChannelOpened.bind(this));
        this.mChannelHandler.addEventListener(cast.receiver.Channel.EventType.CLOSED,this.onChannelClosed.bind(this));
    }

    Presentation.prototype = {

        onChannelOpened: function(event) {
            console.log('onChannelOpened. Total number of channels: ' + this.mChannelHandler.getChannels().length);
        },

        onChannelClosed: function(event) {
            if (this.mChannelHandler.getChannels().length === 0) {
                window.close();
            }
        },

        onMessage: function(event) {
            var message = event.message;
            var channel = event.target;
            console.log('onMessage: %o', message);

            if (message.command === 'start') {
                this.onStart();
            } else if (message.command === 'next' || message.command === 'previous') {
                this.onChange(message.command);
            } else {
                console.log('Invalid message command: ' + message.command);
            }
        },

        onStart: function() {
            var img = document.getElementById('placeholder');
            if (!img) {
                img = document.createElement('img');
                img.id = 'placeholder';
                img.src = 'img/' + images[0];
                img.alt = 'slide';
                document.body.appendChild(img);
                currentImageIndex = 0;
            }
        },

        onChange: function(command) {
            var img = document.getElementById('placeholder');
            if (command === 'next') {
                currentImageIndex = (currentImageIndex + 1) % images.length;
            } else {
                currentImageIndex = (currentImageIndex + images.length - 1) % images.length;
            }
            img.src = 'img/' + images[currentImageIndex];
        }
    };

    cast.Presentation = Presentation;
})();