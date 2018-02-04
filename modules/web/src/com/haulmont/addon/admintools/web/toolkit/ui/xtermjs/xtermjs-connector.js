com_haulmont_addon_admintools_web_toolkit_ui_xtermjs_XtermJsComponent = function() {
    var connector = this;

    Terminal.applyAddon(fit);
    var term = new Terminal({
        cursorBlink: true
    });
    term.open(connector.getElement());

    term.on('data', function (data) {
        connector.data(data);
    });

    connector.write = function (text) {
        term.write(text)
    };

    connector.writeln = function (text) {
        term.writeln(text)
    };

    connector.fit = function () {
        term.fit();
    }
};