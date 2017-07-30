<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="message" value="${message}"/>
<c:set var="cities" value="${cities}"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Welcome</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <!-- /container -->
    <script src="${contextPath}/resources/js/bootstrap.min.js"></script>

    <%--<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>--%>
    <%--<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>--%>

    <script data-require="angular.js@*" data-semver="1.4.0-rc.0" src="https://code.angularjs.org/1.4.0-rc.0/angular.js"></script>

    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyD-KmQUcMJUpRzjthK1CvNtmYw3mLf9vzs&libraries=places&callback=initAutocomplete"
            async defer></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.1.0/js.cookie.js"></script>
    <script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
    <script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"></script>
</head>

<body>

<div class="container">
    <div class="alert alert-danger alert-dismissable fade in" id="message">
        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
        <span id="textMessage">
        </span>
    </div>

    <%--MODAL welcome--%>
    <div class="modal fade" id="openModal" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title" id="modalName">Welcome!</h4>
                </div>
                <div class="modal-body" id="modalData">
                    <p>
                        This is a demo version of application for testing weather providers.<br />
                        <br />
                        Some weather providers with free API was selected for getting forecasts and comparing forecasts data with actual weather for each date.<br />
                        <br />
                        In this app you can determine best weather provider and make analysis weather of last and next 7 days.
                        Also you can see rating of providers by average mistakes for each of them on a current moment.
                        Any date on each page can be opened for detailed analysis.<br />
                        <br />
                        For update weather data you can click update links on top main page.<br />
                        <br />
                        Have a sunny day! :)
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <div id="wrapper" class="">
        <%--open side panel with list cities--%>
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li class="sidebar-brand">
                    <a href="#">List of cities already tested:</a>
                </li>

                <c:forEach items="${cities}" var="textCity" varStatus="loop">
                    <li><a href="#" onclick="takeCity('${textCity}')">${textCity}</a></li>
                </c:forEach>
            </ul>
        </div>

        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="search">
                    <p><strong>Enter your city: </strong>
                        <input id="pac-input" class="controls" type="text" placeholder="Your city">
                    </p>
                    <p><strong>&</strong></p>
                    <p>
                        <button id="testWeather" type="button" class="btn" onclick="getCity()"
                                data-toggle="tooltip" title="Click for analyse weather for this place">
                            <strong>test weather</strong>
                        </button>
                    </p>
                    <c:if test="${cities != null}">
                        <p>
                            <button href="#menu-toggle" class="btn btn-default" id="menu-toggle">Open list of cities
                                <br>
                                <small>already tested</small>
                            </button>
                        </p>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>

<script>
    isOnline();

    $(document).ready(function(){
        setTimeout(function(){
            if(!Cookies.get('modalShown')) {
                $("#openModal").modal('show');
                Cookies.set('modalShown', true);
            } else {
                //alert('Your modal won\'t show again as it\'s shown before.');
            }
        },1000);
    });
</script>

<script>
    var place = document.getElementById('pac-input');
    function initAutocomplete() {
        // Create the search box and link it to the UI element.
        var searchBox = new google.maps.places.SearchBox(place);
    }
    function getCity() {
        var city = place.value;
        var lat, lng, address;
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode( { 'address': city }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                lat = results[0].geometry.location.lat();
                lng = results[0].geometry.location.lng();
                address = results[0].formatted_address;

                openMain(address, lat, lng);
            } else {
                alert("Something got wrong with Google API: " + status);
            }
        });
    }

    function openMain(address, lat, lng) {
        var latRound = roundDouble(lat, 4);
        var lngRound = roundDouble(lng, 4);
        $.ajax({
            url: '${contextPath}/place',
            type: 'GET',
            data: {
                address:address,
                lat:latRound,
                lng:lngRound
            }
        }).success(function (resp) {
            window.location.assign("${contextPath}/main?cityid=" + resp);
        }).error(function (resp) {
            openModal("Error open main:", resp);
        })
    }

    function roundDouble(num, signs) {
        return Math.round(num * 10 * signs) / (10 * signs);
    }


<%--for message "check internet connection"--%>
    function isOnline() {
        var request = new XMLHttpRequest();
        request.open("GET", "https://httpbin.org/", true);
        request.onerror = function () {
            $("#textMessage").html("Check internet connection");
            $("#message").show();
        };
        request.send();
    }
</script>

<%--panel with list cities--%>
<script>
    $("#wrapper").toggleClass("toggled");
    $("#menu-toggle").click(function(e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    function takeCity(textCity) {
        document.getElementById('pac-input').value = textCity;
        $("#wrapper").toggleClass("toggled");
    }
</script>

<style>

    body {
        /*background-image: url("${contextPath}/resources/images/Sky_Background-56.jpg");
        background-repeat:no-repeat;
        background-size:cover;
        background-position:center top;*/
        background: url('${contextPath}/resources/images/Sky_Background-56.jpg') no-repeat fixed;
        -webkit-background-size: cover;
        -moz-background-size: cover;
        -o-background-size: cover;
        background-size: cover;
    }

    .search {
        margin: 100px auto;
        width: 200px;
        position: relative;
        text-align:center;
    }

    .modal-content {
        text-align:left;
    }

    .alert{
        display: none;
    }

    /*for side panel*/
    /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */

    .as-header-wrapper{
        overflow-x: hidden;
    }
    /*!
     * Start Bootstrap - Simple Sidebar HTML Template (http://startbootstrap.com)
     * Code licensed under the Apache License v2.0.
     * For details, see http://www.apache.org/licenses/LICENSE-2.0.
     */

    /* Toggle Styles */
    #wrapper {
        padding-left: 0;
        -webkit-transition: all 0.5s ease;
        -moz-transition: all 0.5s ease;
        -o-transition: all 0.5s ease;
        transition: all 0.5s ease;
    }
    #wrapper.toggled {
        padding-left: 250px;
    }
    #sidebar-wrapper {
        z-index: 1000;
        position: fixed;
        left: 250px;
        width: 0;
        height: 100%;
        margin-left: -250px;
        overflow-y: auto;
        background: #000;
        -webkit-transition: all 0.5s ease;
        -moz-transition: all 0.5s ease;
        -o-transition: all 0.5s ease;
        transition: all 0.5s ease;
    }
    #wrapper.toggled #sidebar-wrapper {
        width: 250px;
    }
    #page-content-wrapper {
        width: 100%;
        position: absolute;
        padding: 15px;
    }
    #wrapper.toggled #page-content-wrapper {
        position: absolute;
        margin-right: -250px;
    }
    /* Sidebar Styles */
    .sidebar-nav {
        position: absolute;
        top: 0;
        width: 250px;
        margin: 0;
        padding: 0;
        list-style: none;
    }
    .sidebar-nav li {
        text-indent: 20px;
        line-height: 40px;
    }
    .sidebar-nav li a {
        display: block;
        text-decoration: none;
        color: #999999;
    }
    .sidebar-nav li a:hover {
        text-decoration: none;
        color: #fff;
        background: rgba(255, 255, 255, 0.2);
    }
    .sidebar-nav li a:active, .sidebar-nav li a:focus {
        text-decoration: none;
    }
    .sidebar-nav > .sidebar-brand {
        height: 65px;
        font-size: 18px;
        line-height: 60px;
    }
    .sidebar-nav > .sidebar-brand a {
        color: #999999;
    }
    .sidebar-nav > .sidebar-brand a:hover {
        color: #fff;
        background: none;
    }
    @media(min-width:768px) {
        #wrapper {
            padding-left: 250px;
        }
        #wrapper.toggled {
            padding-left: 0;
        }
        #sidebar-wrapper {
            width: 250px;
        }
        #wrapper.toggled #sidebar-wrapper {
            width: 0;
        }
        #page-content-wrapper {
            padding: 20px;
            position: relative;
        }
        #wrapper.toggled #page-content-wrapper {
            position: relative;
            margin-right: 0;
        }
    }

    #map {
        height: 100%;
    }
    /* Optional: Makes the sample page fill the window. */
    html, body {
        height: 100%;
        margin: 0;
        padding: 0;
    }

    #infowindow-content .title {
        font-weight: bold;
    }

    #infowindow-content {
        display: none;
    }

    #map #infowindow-content {
        display: inline;
    }

    /*.pac-card {
        margin: 10px 10px 0 0;
        border-radius: 2px 0 0 2px;
        box-sizing: border-box;
        -moz-box-sizing: border-box;
        outline: none;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
        background-color: #fff;
        font-family: Roboto;
    }

    #pac-container {
        padding-bottom: 12px;
        margin-right: 12px;
    }

    .pac-controls {
        display: inline-block;
        padding: 5px 11px;
    }

    .pac-controls label {
        font-family: Roboto;
        font-size: 13px;
        font-weight: 300;
    }

    #pac-input {
        background-color: #fff;
        font-family: Roboto;
        font-size: 15px;
        font-weight: 300;
        margin-left: 12px;
        padding: 0 11px 0 13px;
        text-overflow: ellipsis;
        width: 400px;
    }

    #pac-input:focus {
        border-color: #4d90fe;
    }

    #title {
        color: #fff;
        background-color: #4d90fe;
        font-size: 25px;
        font-weight: 500;
        padding: 6px 12px;
    }
    #target {
        width: 345px;
    }*/

</style>