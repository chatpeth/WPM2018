using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using uPLibrary.Networking.M2Mqtt;
using uPLibrary.Networking.M2Mqtt.Messages;
//using System.Net;

namespace wpm2018a_v1
{
    public partial class Form1 : Form
    {
        private MqttClient _Client;
        private String my_broker = "test.mosquitto.org";
        private String NID_str;
        private Node node = new Node();
        private Zone zone = new Zone();

        private byte connection_status = 1;
        
        //int port = 1883;
        public Form1()
        {
            InitializeComponent();
            //_Client = new MqttClient(IPAddress.Parse("192.168.100.11"));
            _Client = new MqttClient(my_broker);
            //byte connection_result = _Client.Connect(Guid.NewGuid().ToString());
            //_Client.Subscribe(new[] { "fff1/pa" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            _Client.ConnectionClosed += _Client_ConnectionClosed;
            connection_status = _Client.Connect(Guid.NewGuid().ToString());
            _Client.Subscribe(new[] { "505atk"}, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            _Client.MqttMsgPublishReceived += _Client_MqttMsgPublishReceived;

        }
        public class Node
        {
            public string id { get; set; }
            public float pa { get; set; }
            public float pb { get; set; }
            public float pc { get; set; }
            public float pt { get; set; }
            public float pavg { get; set; }
            public float pavgmin { get; set; }
            public float ea { get; set; }
            public float eb { get; set; }
            public float ec { get; set; }
            public float et { get; set; }
            public float peaka { get; set; }
            public float peakb { get; set; }
            public float peakc { get; set; }
            public float peakt { get; set; }

        }
        public class Zone
        {
            public string ZID { get; set; }
            public Node[] Node = new Node[10];
            
        }
        private void _Client_ConnectionClosed(object sender, EventArgs e)
        {
            
            connection_status = _Client.Connect(Guid.NewGuid().ToString());
            if(connection_status == 0)
            {
                MessageBox.Show("Connection restored.");
            }
        }
        private void _Client_MqttMsgPublishReceived(object sender, MqttMsgPublishEventArgs e)
        {
            BeginInvoke(new Action(() =>
            {
                
                String payload = Encoding.UTF8.GetString(e.Message);
                String topic = e.Topic;             

                Payload_lbl.Text = payload;
                Topic_lbl.Text = topic;
                String[] topic_level = topic.Split('/');
                float value = Convert.ToSingle(payload);
                node.id = topic_level[0];

                if (topic_level[1] == "p")
                {
                    if (topic_level[2] == "a")
                    {
                        node.pa = value;                       
                    }
                    else if (topic_level[2] == "b")
                    {
                        node.pb = value;                        
                    }
                    else if (topic_level[2] == "c")
                    {
                        node.pc = value;                       
                    }
                    else if (topic_level[2] == "3phase")
                    {
                        node.pt = value;
                    }
                }
                else if (topic_level[1] == "pavg")
                {
                    if (topic_level[2] == "3phase")
                    {
                        node.pavg = value;
                    }
                    else if (topic_level[2] == "min")
                    {
                        node.pavgmin = value;
                    }
                }
                else if (topic_level[1] == "e")
                {
                    if (topic_level[2] == "a")
                    {
                        node.ea = value;
                    }
                    else if (topic_level[2] == "b")
                    {
                        node.eb = value;
                    }
                    else if (topic_level[2] == "c")
                    {
                        node.ec = value;
                    }
                    else if (topic_level[2] == "3phase")
                    {
                        node.et = value;
                    }
                }
                else if (topic_level[1] == "peak")
                {
                    if (topic_level[2] == "a")
                    {
                        node.peaka = value;
                    }
                    else if (topic_level[2] == "b")
                    {
                        node.peakb = value;
                    }
                    else if (topic_level[2] == "c")
                    {
                        node.peakc = value;
                    }
                    else if (topic_level[2] == "3phase")
                    {
                        node.peakt = value;
                    }
                }
                if (topic_level[0] == NID_str)
                {
                    PA_data.Text = node.pa.ToString();
                    PB_data.Text = node.pb.ToString();
                    PC_data.Text = node.pc.ToString();
                    PT_data.Text = node.pt.ToString();

                    Pavg_data.Text = node.pavg.ToString();
                    Pavg_min_data.Text = node.pavgmin.ToString();

                    EA_data.Text = node.ea.ToString();
                    EB_data.Text = node.eb.ToString();
                    EC_data.Text = node.ec.ToString();
                    ET_data.Text = node.et.ToString();

                    PeakA_data.Text = node.peaka.ToString();
                    PeakB_data.Text = node.peakb.ToString();
                    PeakC_data.Text = node.peakc.ToString();
                    PeakT_data.Text = node.peakt.ToString();

                }
                
            }));
            

        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            _Client.Publish("led", new[] { checkBox1.Checked ? (byte)1 : (byte)0 });
        }

        private void panel1_Paint(object sender, PaintEventArgs e)
        {

        }

        private void label1_Click(object sender, EventArgs e)
        {
            Payload_lbl.Text = "Hello";
        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void label3_Click(object sender, EventArgs e)
        {

        }

        private void label3_Click_1(object sender, EventArgs e)
        {

        }

        private void Connect_btn_Click(object sender, EventArgs e)
        {
            
            String report_msg; 
            NID_str = NID_textbox.Text;

            
            _Client.Subscribe(new[] { NID_str + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            
           if(connection_status == 0)
            {
                report_msg = " conected";
            }
           else
            {
                report_msg = " not connect ERR:0x01";
            }
            MessageBox.Show(NID_str + report_msg);

            Current_node_lbl.Text = NID_str;

            node.pa = 0;
            node.pb = 0;
            node.pc = 0;
            node.pt = 0;
            node.pavg = 0;
            node.pavgmin = 0;
            node.ea = 0;
            node.eb = 0;
            node.ec = 0;
            node.et = 0;
            node.peaka = 0;
            node.peakb = 0;
            node.peakc = 0;
            node.peakt = 0;
        }

        private void button1_Click(object sender, EventArgs e)
        {

        }

        public class ComboboxItem
        {
            public string NID { get; set; }
            public object Description { get; set; }

            public override string ToString()
            {
                return NID + "/" + Description;
            }
        }

        private void Add_btn_Click(object sender, EventArgs e)
        {
            ComboboxItem item = new ComboboxItem();
            item.NID = NID_textbox.Text;
            item.Description = Description_txt.Text;
            Device_lst.SelectedIndex = Device_lst.Items.Add(item);

            //MessageBox.Show((Device_lst.SelectedItem as ComboboxItem).Description.ToString());
        }

        private void Remove_btn_Click(object sender, EventArgs e)
        {
            Device_lst.Items.Remove(Device_lst.SelectedItem);           
        }

        private void Device_lst_SelectedIndexChanged(object sender, EventArgs e)
        {
            
            NID_textbox.Text = (Device_lst.SelectedItem as ComboboxItem).NID.ToString();
            
            
            Description_txt.Text = (Device_lst.SelectedItem as ComboboxItem).Description.ToString();
            if(List_txt.Text == "")
            {
                List_txt.Text = NID_textbox.Text;
            }
            else
            {
                List_txt.Text = List_txt.Text + "," + NID_textbox.Text;
            }
            
        }

        private void splitContainer1_Panel1_Paint(object sender, PaintEventArgs e)
        {

        }

        public class ZoneItem
        {
            public string ZID { get; set; }
            public object List { get; set; }

            public override string ToString()
            {
                return ZID + "/" + List;
            }
        }
        private void Add_zone_btn_Click(object sender, EventArgs e)
        {
            ZoneItem item = new ZoneItem();
            item.ZID = Zone_txt.Text;
            item.List = List_txt.Text;
            Zone_lst.SelectedIndex = Zone_lst.Items.Add(item);
        }

        private void Remove_zone_btn_Click(object sender, EventArgs e)
        {
            Zone_lst.Items.Remove(Zone_lst.SelectedItem);
            
        }

        private void Zone_lbl_Click(object sender, EventArgs e)
        {

        }

        private void zone_connect_btn_Click(object sender, EventArgs e)
        {
            String zone_str;
            
            
            try
            {
                zone_str = Zone_lst.SelectedItem.ToString();
            }
            catch
            {
                MessageBox.Show("Please add new zone");
                return;
            }
            
            String zone_name = zone_str.Split('/')[0];
            zone_str = zone_str.Split('/')[1];
            zone_str = zone_str.Replace(" ", string.Empty);
            String[] zone_list = zone_str.Split(',');
            foreach(String node_id in zone_list)
            {
                System.Console.WriteLine(node_id);
                _Client.Subscribe(new[] { node_id + "/#" }, new byte[] { MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE });
            }
            for(int i = 0; i < zone_list.Length; i++)
            {
                System.Console.WriteLine(i);
                zone.Node[i] = new Node();
                zone.Node[i].id = zone_list[i];
                System.Console.WriteLine(zone.Node[i].id);
            }
        }
    }
}
