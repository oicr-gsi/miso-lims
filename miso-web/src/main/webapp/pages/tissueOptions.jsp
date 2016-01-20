<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>
  
<%@ include file="../header.jsp" %>

<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />

<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/tissueOptions_ajax.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

  <h1>Institute Options</h1>
  
  <ul>
    <li><a href="#origins">Tissue Origins</a></li>
    <li><a href="#conditions">Tissue Conditions</a></li>
    <li><a href="#materials">Tissue Materials</a></li>
    <li><a href="#purposes">Sample Purposes</a></li>
    <li><a href="#qcDetails">QC Details</a></li>
    <li><a href="#subprojects">Subprojects</a></li>
    <li><a href="#classes">Sample Classes</a></li>
  </ul>
  
  <div class="sectionDivider"></div>
  <h2 id="origins">Tissue Origins</h2>
  <div>
	  <table id="allOriginsTable" class="tissueOptionsTable clear">
		  <thead>
			  <tr>
			    <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allOrigins"></tbody>
	  </table>
  </div>
  
  
  <div class="sectionDivider"></div>
  <h2 id="conditions">Tissue Conditions</h2>
  <div>
	  <table id="allConditionsTable" class="tissueOptionsTable clear">
	    <thead>
	      <tr>
	        <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allConditions"></tbody>
	  </table>
	</div>
	
	<div class="sectionDivider"></div>
  <h2 id="materials">Tissue Materials</h2>
  <div>
    <table id="allMaterialsTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allMaterials"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="purposes">Sample Purposes</h2>
  <div>
    <table id="allPurposesTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allPurposes"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="qcDetails">QC Details</h2>
  <div>
    <table id="allQcDetailsTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Description</th><th>QC Passed</th><th>Note Required?</th>
        </tr>
      </thead>
      <tbody id="allQcDetails"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="subprojects">Subprojects</h2>
  <div>
    <table id="allSubprojectsTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Project</th><th>Alias</th><th>Description</th><th>Priority</th>
        </tr>
      </thead>
      <tbody id="allSubprojects"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="classes">Sample Classes</h2>
  <div>
    <table id="allClassesTable" class= "tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Class</th><th>Category</th>
        </tr>
      </thead>
      <tbody id="allClasses"></tbody>
    </table>
  </div>

</div>
</div>
  
<script type="text/javascript">
  jQuery(document).ready(function() {
    Tissue.getTissueOrigins();
    Tissue.getTissueConditions();
    Tissue.getTissueMaterials();
    Tissue.getSamplePurposes();
    QC.getQcDetails();
    Subproject.getProjects();
    Hierarchy.getSampleCategories();
  });
</script>
  
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>