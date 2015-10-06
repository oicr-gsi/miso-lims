function validate_box(form) {
  Fluxion.doAjax(
    'boxControllerHelperService',
    'validateBoxInput',
    {'alias':jQuery('#alias').val(),
    'rows':jQuery('#rows').val(),
    'columns':jQuery('#columns').val(),
    'url':ajaxurl},
    {
      'doOnSuccess': function(json) { if (json.response === "OK") { form.submit(); }},
      'doOnError':function(json) { alert(json.error); }
    }
  );
}
