/**
 * Working Velocity Test - Demonstrates the CORRECT API usage
 */

var module = rise.registerModule("Velocity Test", "Tests packet modification with readable API");

module.handle("onEnable", function() {
    rise.displayChat("§aVelocity Test enabled - watching for velocity packets");
});

module.handle("onPacketReceive", function(e) {
    // Check if it's a velocity packet
    if (e.isPacket("S12PacketEntityVelocity")) {
        var pkt = e.getPacket();
        
        // Check if it's for our player
        if (pkt.getEntityID() == player.getEntityId()) {
            rise.displayChat("§eVelocity detected!");
            
            // Get current velocity
            var motionX = pkt.getMotionX();
            var motionY = pkt.getMotionY();
            var motionZ = pkt.getMotionZ();
            
            rise.displayChat("§7Original: X=" + motionX.toFixed(2) + " Y=" + motionY.toFixed(2) + " Z=" + motionZ.toFixed(2));
            
            // Reduce by 50%
            pkt.setMotionX(motionX * 0.5);
            pkt.setMotionY(motionY * 0.5);
            pkt.setMotionZ(motionZ * 0.5);
            
            rise.displayChat("§aModified to 50%%!");
        }
    }
});

script.handle("onUnload", function() {
    module.unregister();
});
