package ch.sr35.touchsamplesynth.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import ch.sr35.touchsamplesynth.TAG
import java.io.IOException
import java.net.DatagramSocket

import java.net.ServerSocket

class NetworkDiscoveryHandler(context: Context): NsdManager.RegistrationListener,NsdManager.DiscoveryListener,NsdManager.ResolveListener {

    private var serviceName: String="TouchSampleSynth Midi"
    private val serviceType: String="_apple-midi._udp"
    private var discoveredServices: ArrayList<NsdServiceInfo>
    private var nsdManager:  NsdManager?=null

    init {
        nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager)
        discoveredServices=ArrayList()
    }
    fun registerService(portNr: Int=0)
    {
        val serviceInfo=NsdServiceInfo()
        serviceInfo.serviceName=serviceName
        serviceInfo.serviceType = serviceType

        if (portNr==0) {
            var controlPort = 1024
            var portsFound = false
            while (!portsFound && controlPort < 65535) {
                try {
                    DatagramSocket(controlPort)
                    ServerSocket(controlPort + 1)
                    portsFound = true
                } catch (_: IOException) {

                }
                controlPort += 1
            }

            serviceInfo.port = controlPort - 1
        }
        else
        {
            serviceInfo.port=portNr
        }

        nsdManager?.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, this)
    }

    fun tearDown()
    {
        nsdManager?.unregisterService(this)
        nsdManager?.stopServiceDiscovery(this)
    }


    fun discoverServices()
    {
        nsdManager?.discoverServices("_apple-midi._udp", NsdManager.PROTOCOL_DNS_SD, this)
    }

    override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
        // Save the service name. Android may have changed it in order to
        // resolve a conflict, so update the name you initially requested
        // with the name Android actually used.
        serviceName = NsdServiceInfo.serviceName
    }

    override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        // Registration failed! Put debugging code here to determine why.
    }

    override fun onServiceUnregistered(arg0: NsdServiceInfo) {
        // Service has been unregistered. This only happens when you call
        // NsdManager.unregisterService() and pass in this listener.
    }

    override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        // Unregistration failed. Put debugging code here to determine why.
    }


    override fun onDiscoveryStarted(regType: String) {
        Log.d(TAG, "Service discovery started")
    }

    override fun onServiceFound(service: NsdServiceInfo) {
        // A service was found! Do something with it.
        Log.d(TAG, "Service discovery success$service")
        when {
            service.serviceType == this.serviceType -> // Service type is the string containing the protocol and
                // transport layer for this service.
            {
                Log.d(TAG, "Discovered apple midi: ${service.serviceType}")
                nsdManager?.resolveService (service, this)
            }
            //service.serviceName == mServiceName -> // The name of the service tells the user what they'd be
            // connecting to. It could be "Bob's Chat App".
            //    Log.d(TAG, "Same machine: $mServiceName")
            //service.serviceName.contains("NsdChat") -> nsdManager.resolveService(service, resolveListener)
        }
    }

    override fun onServiceLost(service: NsdServiceInfo) {
        // When the network service is no longer available.
        // Internal bookkeeping code goes here.
        Log.e(TAG, "service lost: $service")
        if (service in this.discoveredServices)
        {
            discoveredServices.remove(service)
        }
    }

    override fun onDiscoveryStopped(serviceType: String) {
        Log.i(TAG, "Discovery stopped: $serviceType")
    }

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        Log.e(TAG, "Discovery failed: Error code:$errorCode")
        nsdManager?.stopServiceDiscovery(this)
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        Log.e(TAG, "Discovery failed: Error code:$errorCode")
        nsdManager?.stopServiceDiscovery(this)
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        Log.e(TAG, "Resolve Succeeded. $serviceInfo")

        if (serviceInfo.serviceName == serviceName) {
            Log.d(TAG, "Same IP.")
            return
        }
        if (serviceInfo.serviceType == this.serviceType)
        {
            this.discoveredServices.add(serviceInfo)
        }
    }

    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        // Called when the resolve fails. Use the error code to debug.
        Log.e(TAG, "Resolve failed: $errorCode")
    }
}