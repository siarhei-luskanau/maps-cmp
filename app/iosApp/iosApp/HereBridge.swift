import ComposeApp
import CoreGraphics
import heresdk
import UIKit

final class HereSearchBridgeImpl: NSObject, HereSearchBridge {
    private let engine: SearchEngine

    override init() {
        do {
            engine = try SearchEngine()
        } catch {
            fatalError("Failed to create HERE SearchEngine: \(error)")
        }
        super.init()
    }

    func searchByAddress(query: String, callback: HereSearchCallback) {
        let addressQuery = AddressQuery(query)
        let options = SearchOptions(languageCode: .enUs, maxItems: 20)
        _ = engine.searchByAddress(addressQuery, options: options) { error, places in
            if let error = error {
                callback.onError(message: "\(error)")
                return
            }
            let results: [HereSearchResult] = (places ?? []).compactMap { place in
                guard let coords = place.geoCoordinates else { return nil }
                let label = place.address.addressText.isEmpty ? place.title : place.address.addressText
                return HereSearchResult(
                    placeId: place.id,
                    label: label,
                    latitude: coords.latitude,
                    longitude: coords.longitude
                )
            }
            callback.onSuccess(results: results)
        }
    }
}

final class HereRouteBridgeImpl: NSObject, HereRouteBridge {
    private let routingEngine: RoutingEngine

    override init() {
        do {
            routingEngine = try RoutingEngine()
        } catch {
            fatalError("Failed to create HERE RoutingEngine: \(error)")
        }
        super.init()
    }

    func calculateCarRoute(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
        callback: HereRouteCallback
    ) {
        let waypoints = [
            Waypoint(coordinates: GeoCoordinates(latitude: fromLat, longitude: fromLon)),
            Waypoint(coordinates: GeoCoordinates(latitude: toLat, longitude: toLon))
        ]
        _ = routingEngine.calculateRoute(with: waypoints, carOptions: CarOptions()) { error, routes in
            if let error = error {
                callback.onError(message: "\(error)")
                return
            }
            guard let route = routes?.first else {
                callback.onError(message: "No route found")
                return
            }
            let polyline = route.geometry.vertices.map {
                HereMapsPoint(lat: $0.latitude, lon: $0.longitude)
            }
            callback.onSuccess(result: HereRouteResult(
                polyline: polyline,
                durationSeconds: Int64(route.duration),
                distanceMeters: Int64(route.lengthInMeters)
            ))
        }
    }
}

final class HereMapsViewBridgeImpl: NSObject, HereMapsViewBridge {
    func createMapView(
        lat: Double,
        lon: Double,
        zoom: Float,
        polyline: [HereMapsPoint],
        hasLocation: Bool,
        locationLat: Double,
        locationLon: Double
    ) -> UIView {
        let mapView = MapView()
        mapView.mapScene.loadScene(mapScheme: .normalDay) { mapError in
            guard mapError == nil else { return }
            let zoomMeasure = MapMeasure(kind: .zoomLevel, value: Double(zoom))
            mapView.camera.lookAt(
                point: GeoCoordinates(latitude: lat, longitude: lon),
                zoom: zoomMeasure
            )
            if polyline.count >= 2 {
                let coords = polyline.map { GeoCoordinates(latitude: $0.lat, longitude: $0.lon) }
                if let geoPolyline = try? GeoPolyline(vertices: coords),
                   let mapPolyline = try? MapPolyline(
                       geometry: geoPolyline,
                       representation: MapPolyline.SolidRepresentation(
                           lineWidth: MapMeasureDependentRenderSize(sizeUnit: .pixels, size: 12.0),
                           color: UIColor(red: 0, green: 0.4, blue: 1.0, alpha: 1.0),
                           capShape: .round
                       )
                   ) {
                    mapView.mapScene.addMapPolyline(mapPolyline)
                }
            }
            if hasLocation {
                let locationCoords = GeoCoordinates(latitude: locationLat, longitude: locationLon)
                let renderer = UIGraphicsImageRenderer(size: CGSize(width: 32, height: 32))
                let image = renderer.image { ctx in
                    UIColor.blue.setFill()
                    ctx.cgContext.fillEllipse(in: CGRect(x: 1, y: 1, width: 30, height: 30))
                }
                if let imageData = image.pngData() {
                    let marker = MapMarker(
                        at: locationCoords,
                        image: MapImage(pixelData: imageData, imageFormat: .png)
                    )
                    mapView.mapScene.addMapMarker(marker)
                }
            }
        }
        return mapView
    }
}