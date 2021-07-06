(function () {
    window.query = function(exp){ return document.querySelector(exp); }
    window.queryAll = function(exp){ return document.querySelectorAll(exp); }
    var burger = query('.overflow-menu');
    if(burger){
        var menu = query('#' + burger.dataset.target);
        burger.addEventListener('click', function () {
            burger.classList.toggle('is-active');
            menu.classList.toggle('is-active');
        });
    }
    window.setupDataTables = function(...elements){
        var _results = [];
        var options = {perPage: 50, perPageSelect: [5, 10, 20, 25, 50, 100]};
        for(i=0; i< elements.length;i++){
            _results.push(new simpleDatatables.DataTable(elements[i], options));
        }
        return _results;
    };
    var _dataTable_ = query('.dataTable');
    if(_dataTable_){
        const dataTable = new simpleDatatables.DataTable(
            _dataTable_,
            {
                perPage: 50,
                perPageSelect: [5, 10, 20, 25, 50, 100]
            }
        )
    }

})();