package org.firstinspires.ftc.teamcode.RRAutons.opencv;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

@Config
public class RedPositionDetectorPipeline extends OpenCvPipeline {

    Telemetry telemetry;

    /**
     * The Width of the Camera, defaulted to 320 pixels
     */
    static final int CAMERA_WIDTH = 320;

    public static int red_min = Constants.redSide_red_min;
    public static int red_max = Constants.redSide_red_max;
    public static int green_min = Constants.redSide_green_min;
    public static int green_max = Constants.redSide_green_max;
    public static int blue_min = Constants.redSide_blue_min;
    public static int blue_max = Constants.redSide_blue_max;

    public static int leftDivision = 160; //106 old
    public static int rightDivision = 319; //213 old
    public static int topY = 240;
    public static int maxX = 320;
    public String value = "";

    public int leftValue = 0;
    public int rightValue = 0;

    /**
     * The Divider is used to divide the portion of the area considered and not considered
     */
    int DIVIDER;

    /**
     * This will store the value of the position of the Scoring Element to know where to place it
     */
//    private volatile Position position = Position.Right;
    private volatile Position position = Position.Left;

    public enum Position {
        Left("Left"),
        Center("Center");
//        Right("Right");

        public String val;

        Position(String val) {
            this.val = val;
        }
    }



    public RedPositionDetectorPipeline(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    // TODO: Tune the minimum and maximum detection values
    static final Scalar DETECTION_MINIMUM = new Scalar (red_min, green_min, blue_min);
//    static final Scalar DETECTION_MINIMUM = new Scalar (blue_min, green_min, red_min);
    static final Scalar DETECTION_MAXIMUM = new Scalar (red_max, green_max, blue_max);
//    static final Scalar DETECTION_MAXIMUM = new Scalar (blue_max, green_max, red_max);

    //Stores the frame after it has been converted
    Mat yCrCb = new Mat();

    @Override
    public void init(Mat mat) {
        super.init(mat);
    }


    /**
     * ProcessFrame takes each frame in the video to find the position of the scoring element
     * <p>
     * It first converts to YCrCb and processes the image to find all colors in a certain threshold.
     * After that is done, it calculates the area of target color in each potential area within
     * the frame to find out where the scoring element is positioned.
     *
     * @param input The frame to make calculations off of.
     * @return A processed frame to display on the screen.
     */
    @Override
    public Mat processFrame(Mat input) {
        // The first thing that this does is that it converts the input to YCrCb. The YCrCb color
        // space works much better in this situation because it will help significantly with
        // calculating thresholds

        Imgproc.cvtColor(input, yCrCb, Imgproc.COLOR_RGB2YCrCb);

        // Creates a mask mat, which finds all the images within the range of colors. The mat
        // divides the data into black and white, with white being the pixels in the threshold
        Mat mask = new Mat(yCrCb.rows(), yCrCb.cols(), CvType.CV_8UC1);
        Core.inRange(yCrCb, DETECTION_MINIMUM, DETECTION_MAXIMUM, mask);

        // Use a Gaussian Blur to reduce noise in the frame, such as shadows and lighting, and just
        // make calculations easier.
        Imgproc.GaussianBlur(mask, mask, new Size(5., 15.), 0);

        // TODO: Determine the positions of left, right, and center
        //TODO: Declare variables
        Rect left = new Rect(new Point(0, 0), new Point(leftDivision, topY));
        Rect center = new Rect(new Point(leftDivision, 0), new Point(rightDivision, topY));
//        Rect right = new Rect(new Point(rightDivision, 0), new Point(maxX, topY));


        // Draw a RED Rectangle around each potential area of the item
        //IS WHITE BECAUSE IMAGE IS TURNED INTO 1 STREAM GRAYSCALE
        Imgproc.rectangle(mask, left, new Scalar(255, 255, 255), 5);
        Imgproc.rectangle(mask, center, new Scalar(255, 255, 255), 5);
//        Imgproc.rectangle(mask, right, new Scalar(255, 255, 255), 5);

        // Study each rectangle separately
        Mat leftStudy = new Mat(mask, left);
        Mat centerStudy = new Mat(mask, center);
//        Mat rightStudy = new Mat(mask, right);

        //UP TO HERE IS GOOD


        double leftDensity = (double) Core.countNonZero(leftStudy)/(leftStudy.rows()*leftStudy.cols());
        double centerDensity = (double) Core.countNonZero(centerStudy)/(centerStudy.rows()*centerStudy.cols());
//        double rightDensity = (double) Core.countNonZero(rightStudy)/(rightStudy.rows()*rightStudy.cols());

        leftValue = Core.countNonZero(leftStudy);
        rightValue = Core.countNonZero(centerStudy);



        Position curPos;

//        if (leftDensity > centerDensity && leftDensity > rightDensity){
//            curPos = Position.Left;
//        }
//        else if (centerDensity > rightDensity){
//            curPos = Position.Center;
//        }
//        else{
//            curPos = Position.Right;
//        }

        if (leftDensity >= centerDensity){
            curPos = Position.Left;
        }
        else {
            curPos = Position.Center;
        }


        telemetry.addData("Position Found", curPos.val);
        Constants.position = curPos.val;
        value = curPos.val;
        telemetry.update();
        return mask;
    }

    public String getValue(){
        return value;
    }

    public int getLeftValue() {
        return leftValue;
    }

    public int getRightValue() {
        return rightValue;
    }

    public Position getPosition() {
        return position;
    }
}