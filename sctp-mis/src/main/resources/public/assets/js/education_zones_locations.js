// TODO: refactor this script to be useable everywhere we have the location options for district + t/a.
(function(){
    // Functionality for Card Toggling only for this page..
    let cardToggles = document.getElementsByClassName('card-toggle');
    for (let i = 0; i < cardToggles.length; i++) {
        cardToggles[i].addEventListener('click', e => {
            e.currentTarget.parentElement.parentElement.childNodes[3].classList.toggle('is-hidden');
        });
    }

    let disabledOpt = new Option('Select Option', -1);
    disabledOpt.disable = true;
    window.renderOptions = function(list, promise, prepend, useExtraField){
        list.length = 0;
        if(prepend){
            list.options.add(disabledOpt);
        }
        promise.json().then(items => items.forEach(item => list
            .options.add(new Option(item.text, useExtraField ? item.extra : item.id))));
    };
    window.getOptions = function(url, onSuccess, onError){
        var params = { method: 'GET' };
        try {
            fetch(url, params)
            .then(r => onSuccess(r))
            .catch(e => onError(e));
        }catch(e){
            onError(e);
        }
    };
    window.loadLocations = function(target, sender, prepend, useExtraField){
        getOptions(
            '/locations/get-child-locations?id=' + sender.selectedOptions[0].value,
            function(data){
                renderOptions(target, data, prepend, useExtraField);
            },
            function(error){
                console.log(error);
                alert('There was an error getting locations from the server.');
            }
         );
    };
    districtCode.onchange = function(){ taCode.length = 0; loadLocations(taCode, this, true, true); };
    taCode.onchange = function(){ loadLocations(clusterCode, this, false, true); };
})();
