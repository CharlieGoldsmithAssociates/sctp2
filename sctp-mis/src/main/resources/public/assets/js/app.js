(function () {
    window.query = function (exp) { return document.querySelector(exp); }
    window.queryAll = function (exp) { return document.querySelectorAll(exp); }
    var burger = query('.overflow-menu');
    if (burger) {
        var menu = query('#' + burger.dataset.target);
        burger.addEventListener('click', function () {
            burger.classList.toggle('is-active');
            menu.classList.toggle('is-active');
        });
    }
    window.meta = function (name) {
        var m = query(`meta[name='${name}']`);
        if (m != null) {
            return m.getAttribute('content');
        }
        return null;
    }
    window.csrf = function () {
        return {
            'name': query('meta[name="csrf-name"]').content,
            'token': query('meta[name="csrf-token"]').content
        }
    };
    window.replaceClass = function (el, a, b) {
        if (el.dataset.class == a) {
            el.dataset.class = b;
        } else {
            el.dataset.class = a;
        }
        el.classList.remove(a, b);
        el.classList.add(el.dataset.class);
    };
    window.setupDataTables = function (...elements) {
        var _results = [];
        var options = { perPage: 50, perPageSelect: [5, 10, 20, 25, 50, 100] };
        for (i = 0; i < elements.length; i++) {
            _results.push(new simpleDatatables.DataTable(elements[i], options));
        }
        return _results;
    };
    _dataTable_ = query('.dataTable');
    if (_dataTable_) {
        window.firstDataTable = new simpleDatatables.DataTable(
            _dataTable_,
            {
                perPage: 50,
                perPageSelect: [5, 10, 20, 25, 50, 100]
            }
        )
    }
    HTMLElement.prototype.fireEvent = function (name, opts = null) { this.dispatchEvent(new Event(name, opts || {})); };
    window.templateFromHtml = function (html, format = 'first_child') {
        const formats = ['template', 'first_child', 'child_nodes'];
        var tpl = document.createElement('template');
        tpl.innerHTML = html;
        if (format === 'template') {
            return tpl;
        }
        if (format === 'first_child') {
            return tpl.content.firstChild;
        }
        if (format === 'child_nodes') {
            return tpl.content.childNodes;
        }
        throw 'format parameter must be one of: ' + formats.join(',');
    };
    window.downloadTableToCsv = function (name, skipColumns) {
        var opts = { type: 'csv', download: true, filename: name };
        if (skipColumns) {
            opts['skipColumn'] = skipColumns;
        }
        firstDataTable.export(opts);
    };
    window.createResumableFile = function (file, options) {
        if ("tus" in window) {
            if (!("retryDelays" in options)) {
                options["retryDelays"] = [0, 3000, 5000];
            }
            return new tus.Upload(file, options);
        } else {
            console.error('tus library not loaded.');
            return null;
        }
    };
    window.uploadResumableFile = function (tusUploadHandle, resumeHint = true) {
        if (resumeHint) {
            tusUploadHandle.findPreviousUploads().then(function (previousUploads) {
                if (previousUploads.length) {
                    tusUploadHandle.resumeFromPreviousUpload(previousUploads[0])
                }
                tusUploadHandle.start()
            });
        } else {
            tusUploadHandle.start();
        }
    };
    window.postForm = async function (url, options) {
        const files = 'files' in options ? options.files : null;
        const data = 'data' in options ? options.data : null;
        const hdrs = 'headers' in options
            ? options.headers
            : { /*'Content-Type': 'multipart/form-data; charset=UTF8',*/ 'X-CSRF-TOKEN': csrf().token };
        const form = new FormData();
        if (files) {
            for (var i = 0; i < files.length; i++) {
                form.append('name' in files[i] ? files[i].name : ('file' + i), files[i].file);
            }
        }
        if (data) {
            for (var k in data) {
                form.append(k, data[k]);
            }
        }
        const response = await fetch(url, { method: 'POST', body: form, headers: hdrs });
        if (!response.ok) {
            const message = `An error has occurred: HTTP ${response.status}`;
            throw new Error(message);
        }
        const type = response.headers.get('Content-Type');
        if (type == 'application/json') {
            return await response.json();
        }
        if (type.startsWith('text/')) {
            return await response.text();
        }
        const raw = await response.blob();
        return { contentType: type, blob: raw };
    };
    window.validateFile = function (file, size, types) {
        return file.size <= size && types.indexOf(file.type) != -1;
    };

    window.isJsonContentType = (type) => typeof (type) === 'string' && type.startsWith("application/json");
})();