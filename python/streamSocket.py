import socket
import struct
import cv2
import numpy as np

HOST = ''
PORT = 9876
ADDR = (HOST,PORT)
BUFSIZE = 4096

serv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

serv.bind(ADDR)
serv.listen(5)

print 'listening ...'
cv2.namedWindow("preview")

while True:
	conn, addr = serv.accept()
	print 'client connected ... ', addr
	# myfile = open('testfile.mov', 'w')
	# msgparts = []
	b = bytearray()
	remaining = 1
	while True:
		chunk = conn.recv(BUFSIZE)
		if not chunk: break
		# msgparts.append(chunk)
		b.extend(chunk)

	# msg = b"".join(msgparts) 
	
	# b.extend(msg)
	# nFloats =  len(msg)/4
	#floatlist = struct.unpack('%sf'% nFloats,msg)
	floatlist = np.uint8(b)
	# print floatlist.shape
	if( len(floatlist) == 720 * 1280 *3):
		floatlist.shape = (720, 1280, 3)
	elif ( len(floatlist) == 720 * 1280 *4):
		floatlist.shape = (720, 1280, 4)
	else:
		print "error "+str(floatlist.shape)
		continue
	# print 720 * 1280 *3
	
	
	# print floatlist [700]
	cv2.imshow("preview", floatlist)
	key = cv2.waitKey(20)
	if key == 27: # exit on ESC
		break
	# '''
	
	conn.close()
	print 'client disconnected'
cv2.destroyWindow("preview")

