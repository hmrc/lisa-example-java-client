


var $loginTA = $('#ta'),
    $loginLM = $('#lm');

var $formTA = $('#formTA'),
    $formLM = $('#formLM');

$('#boxes a').on('click', function() {

    if(this.id === 'ta') {
        $formTA.show();
        $formLM.hide();
    }

    if(this.id === 'lm') {
        $formLM.show();
        $formTA.hide();
    }

});

