<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="message" value="${message}"/>

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

        <%--<p>
            <button type="button" class="btn btn-sm btn-primary" onclick="openSidePanel()">Open list of cities
                <br>
                <small>already tested</small>
            </button>
        </p>--%>
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
        var lat, lng;
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode( { 'address': city }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                lat = results[0].geometry.location.lat();
                lng = results[0].geometry.location.lng();

                openMain(city, lat, lng);
            } else {
                alert("Something got wrong with Google API: " + status);
            }
        });
    }
    function openMain(city, lat, lng) {
        var latRound = roundDouble(lat, 4);
        var lngRound = roundDouble(lng, 4);
        $.ajax({
            url: '${contextPath}/place',
            type: 'GET',
            data: {
                city:city,
                lat:latRound,
                lng:lngRound
            }
        }).success(function (resp) {
            window.location.assign("${contextPath}/main?cityid=" + resp);
        }).error(function (resp) {
            openModal("Error open main:", resp);
        })
    }

    function isOnline() {
        var request = new XMLHttpRequest();
        request.open("GET", "https://httpbin.org/", true);
        request.onerror = function () {
            $("#textMessage").html("Check internet connection");
            $("#message").show();
        };
        request.send();
    }

    function roundDouble(num, signs) {
        return Math.round(num * 10 * signs) / (10 * signs);
    }
</script>

<style>

    body {
        background-image: url("${contextPath}/resources/images/Sky_Background-56.jpg");
        background-repeat:no-repeat;
        background-size:cover;
        background-position:center top;
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

</style>