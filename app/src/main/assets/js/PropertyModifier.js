function modifyProperty(className) {
    var element = document.getElementsByTagName('div');
    var result = [];
    for (let i = 0; i < element.length; i++) {
        if (element[i].className != className) {
            continue;
        }
        result.push(element[i]);
    }
    if (result.length === 0) {
        return;
    }
    result[0].style.display = 'none';
}
