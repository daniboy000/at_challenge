import requests
import json

def fetchFromServer(url, payload):
    headers = {"X-AppGlu-Environment": "staging", "Content-Type" : "application/json"}
    auth = ("WKD4N7YMA1uiM8V", "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68")
oeuoueaoeu
    r = requests.post(url, 
        auth = auth, 
        headers = headers,
        data = json.dumps(payload))

    return r.text.encode('utf8')

#----------------------------------------------------------------------------

urlRoute = "https://api.appglu.com/v1/queries/findRoutesByStopName/run"
urlStops = "https://api.appglu.com/v1/queries/findStopsByRouteId/run" 
urlDeparts = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run"

jsonRoute = {"params": {"stopName": "%lauro linhares%"}}
jsonStop = {"params": {"routeId": 22}}

routes = fetchFromServer(urlRoute, jsonRoute)
print "ROUTES: ", routes

stops = fetchFromServer(urlStops, jsonStop)
print "STOPS: ", stops

departs = fetchFromServer(urlDeparts, jsonStop)
print "DEPARTS: ", departs
