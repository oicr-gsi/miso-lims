/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

jQuery(document).ready(function () {
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

// Trim whitespace from input fields
function clean_study_fields() {
  jQuery('#study-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#study-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_study() {
  clean_study_fields();
  // Have to manually add attributes to all form elements because form elements are dynamically generated :/
  jQuery('#study-form').parsley().destroy();

  // Alias input field validation
  jQuery('#alias').attr('class', 'form-control');
  jQuery('#alias').attr('data-parsley-required', 'true');
  jQuery('#alias').attr('data-parsley-maxlength', '100');

  // Description input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '65535');
  jQuery('#study-form').parsley();
  jQuery('#study-form').parsley().validate();
  validate_backend();
};

function validate_backend() {
  updateWarning();
  // if valid, then submit form
  if (true === jQuery('#study-form').parsley().isValid()) {
    jQuery('#study-form').submit();
  }
};

